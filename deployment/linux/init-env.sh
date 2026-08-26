#!/bin/bash
# ============================================================
# 脚本：在 Rocky Linux 上一键部署 Docker + 所有中间件（含 MySQL）
# 功能：安装 Docker，配置镜像加速器，部署 MySQL、RocketMQ、xxl-job-admin、Nacos、Redis，
#       并自动创建所需数据库。
# 特性：出错自动回滚（清理本次创建的容器，不包括 MySQL），镜像拉取自动重试
# 说明：
#   1. 脚本会自动配置 /etc/docker/daemon.json 并重启 Docker。
#   2. MySQL 容器自动创建（若已存在则检查并修复），默认挂载 /opt/mysql/data 持久化。
#   3. xxl-job-admin 使用固定版本 2.4.0，端口 8085。
#   4. Redis 使用最新镜像，端口 6379（无密码，可配置）。
#   5. 所有服务均使用宿主机 IP 进行连接。
# ============================================================

set -e
trap 'rollback' ERR

# ---------- 可修改的配置变量 ----------
MYSQL_ROOT_PASSWORD="root"          # MySQL root 密码
MYSQL_PORT="3306"                   # MySQL 端口（宿主机映射）
MYSQL_DATA_DIR="/opt/mysql/data"    # MySQL 数据持久化目录（若为空则使用匿名卷）
ROCKETMQ_NAMESRV_PORT="9876"
ROCKETMQ_BROKER_PORT="10911"
XXL_JOB_PORT="8085"
NACOS_PORT="8080"                   # Nacos HTTP 访问端口（宿主机），容器内为 8080
NACOS_GPRC="9848"
NACOS_EXTRA_PORT="8848"             # 额外映射的 TCP 端口（按您要求保留）
REDIS_PORT="6379"
REDIS_PASSWORD=""                   # Redis 密码（留空表示无密码）

# ---------- 全局变量 ----------
HOST_IP=""
MYSQL_HOST=""                       # MySQL 连接地址（使用宿主机 IP）

# ---------- 函数：获取宿主机 IP ----------
get_host_ip() {
    HOST_IP=$(ip -4 addr show scope global | grep inet | awk '{print $2}' | cut -d/ -f1 | head -n1)
    if [ -z "$HOST_IP" ]; then
        HOST_IP=$(hostname -I | awk '{print $1}')
    fi
    if [ -z "$HOST_IP" ]; then
        echo "❌ 无法获取宿主机 IP，请检查网络配置。"
        exit 1
    fi
    echo "✅ 检测到宿主机 IP：$HOST_IP"
}

# ---------- 函数：拉取镜像（带重试） ----------
pull_image() {
    local image="$1"
    local retries=3
    local count=0
    while [ $count -lt $retries ]; do
        count=$((count+1))
        echo "正在拉取镜像 $image (尝试 $count/$retries) ..."
        if docker pull "$image"; then
            echo "✅ 镜像 $image 拉取成功。"
            return 0
        fi
        echo "⚠️ 镜像拉取失败，等待 5 秒后重试..."
        sleep 5
    done
    echo "❌ 镜像 $image 拉取失败，已重试 $retries 次。"
    return 1
}

# ---------- 函数：回滚（清理本次创建的容器，不操作 MySQL） ----------
rollback() {
    echo -e "\n❌ 安装过程出现错误，开始执行回滚..."
    for container in rocketmq-namesrv rocketmq-broker xxl-job-admin nacos redis; do
        docker stop "$container" 2>/dev/null || true
        docker rm "$container" 2>/dev/null || true
    done
    echo "回滚完成。"
    exit 1
}

# ---------- 函数：确保 MySQL 容器正常运行（不存在则创建，存在则修复） ----------
ensure_mysql() {
    echo "=========================================="
    echo "检查 MySQL 容器状态..."

    # 检查容器是否存在
    if docker ps -a --format '{{.Names}}' | grep -q "^mysql$"; then
        # 容器存在
        if docker ps --format '{{.Names}}' | grep -q "^mysql$"; then
            echo "✅ MySQL 容器已在运行。"
        else
            echo "⚠️  MySQL 容器存在但未运行，尝试启动..."
            if docker start mysql; then
                sleep 5
                if docker ps --format '{{.Names}}' | grep -q "^mysql$"; then
                    echo "✅ MySQL 容器已启动。"
                else
                    echo "❌ 启动失败，容器未能正常运行。将删除并重新创建..."
                    docker rm -f mysql
                    create_mysql
                fi
            else
                echo "❌ 启动失败（docker start 报错），将删除并重新创建..."
                docker rm -f mysql
                create_mysql
            fi
        fi
    else
        # 容器不存在，直接创建
        echo "MySQL 容器不存在，正在创建..."
        create_mysql
    fi

    # 检查端口映射
    local mapped_port=$(docker port mysql 3306 | head -n1 | cut -d ':' -f2)
    if [ -z "$mapped_port" ]; then
        echo "⚠️  MySQL 容器未映射 3306 端口，将删除并重新创建（带端口映射）..."
        docker rm -f mysql
        create_mysql
        mapped_port=$(docker port mysql 3306 | head -n1 | cut -d ':' -f2)
    fi
    echo "✅ MySQL 端口映射：3306 -> $mapped_port"
    if [ "$mapped_port" != "3306" ]; then
        MYSQL_PORT="$mapped_port"
        echo "⚠️  使用映射端口 $MYSQL_PORT"
    fi

    # 等待 MySQL 完全启动并测试本地连接
    echo "等待 MySQL 就绪（最多 30 秒）..."
    local retry=0
    while ! docker exec mysql mysqladmin ping -h localhost -u root -p"$MYSQL_ROOT_PASSWORD" --silent 2>/dev/null; do
        retry=$((retry+1))
        if [ $retry -ge 30 ]; then
            echo "❌ MySQL 启动超时，请检查日志。"
            docker logs mysql --tail 20
            exit 1
        fi
        sleep 1
    done
    echo "✅ MySQL 本地连接已就绪。"

    # 额外等待 3 秒让 MySQL 完全准备好外部连接
    sleep 3
}

# ---------- 函数：创建 MySQL 容器 ----------
create_mysql() {
    echo "创建 MySQL 容器（端口 $MYSQL_PORT，密码 $MYSQL_ROOT_PASSWORD）..."
    local volume_opts=""
    if [ -n "$MYSQL_DATA_DIR" ]; then
        mkdir -p "$MYSQL_DATA_DIR"
        volume_opts="-v $MYSQL_DATA_DIR:/var/lib/mysql"
        echo "数据目录：$MYSQL_DATA_DIR"
    fi

    docker run -d --name mysql \
        -p "$MYSQL_PORT":3306 \
        -e MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD" \
        $volume_opts \
        --restart=unless-stopped \
        mysql:latest

    sleep 3
    if ! docker ps --format '{{.Names}}' | grep -q "^mysql$"; then
        echo "❌ 创建 MySQL 容器失败。"
        docker logs mysql --tail 20
        exit 1
    fi
    echo "✅ MySQL 容器创建成功。"
}

# ---------- 函数：测试 MySQL 外部连接（带重试） ----------
test_mysql_connection() {
    local max_retries=3
    local count=0
    echo "测试 MySQL 外部连接（从临时容器连接 $MYSQL_HOST:$MYSQL_PORT）..."
    while [ $count -lt $max_retries ]; do
        count=$((count+1))
        echo "尝试 $count/$max_retries ..."
        if docker run --rm mysql:latest \
            mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" \
            -e "SELECT 1" >/dev/null 2>&1; then
            echo "✅ MySQL 外部连接正常。"
            return 0
        fi
        # 打印详细错误（第一次重试时显示详细错误）
        if [ $count -eq 1 ]; then
            echo "⚠️  连接失败，详细信息如下（仅显示一次）："
            docker run --rm mysql:latest \
                mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" \
                -e "SELECT 1" 2>&1 | head -5 || true
        fi
        echo "等待 3 秒后重试..."
        sleep 3
    done
    echo "❌ 无法连接到 MySQL，经过 $max_retries 次尝试仍失败。"
    echo "请检查："
    echo "   - MySQL 是否运行在 $MYSQL_HOST:$MYSQL_PORT"
    echo "   - 防火墙是否放行该端口（宿主机和容器内）"
    echo "   - root 密码是否正确"
    echo "   - MySQL 是否允许外部连接（bind-address 应为 0.0.0.0）"
    echo "可尝试在宿主机执行：mysql -h $MYSQL_HOST -P $MYSQL_PORT -u root -p'$MYSQL_ROOT_PASSWORD'"
    exit 1
}

# ---------- 1. 检查并安装 Docker ----------
echo "=========================================="
echo "检查 Docker 安装状态..."
if ! command -v docker &> /dev/null; then
    echo "Docker 未安装，开始安装..."
    dnf install -y yum-utils device-mapper-persistent-data lvm2
    dnf install -y docker-ce docker-ce-cli containerd.io
    systemctl start docker
    systemctl enable docker
    echo "✅ Docker 安装完成。"
else
    echo "✅ Docker 已安装。"
fi

# 确保 Docker 服务正在运行
if ! docker info >/dev/null 2>&1; then
    echo "Docker 服务未运行，尝试启动..."
    systemctl start docker
    sleep 3
    if ! docker info >/dev/null 2>&1; then
        echo "❌ Docker 服务启动失败，请手动检查。"
        exit 1
    fi
fi
echo "✅ Docker 服务运行正常。"

# ---------- 配置 Docker 镜像加速器 ----------
echo "=========================================="
echo "配置 Docker 镜像加速器..."
mkdir -p /etc/docker
if [ -f /etc/docker/daemon.json ]; then
    cp /etc/docker/daemon.json /etc/docker/daemon.json.bak.$(date +%s)
    echo "已备份原有 daemon.json"
fi
cat > /etc/docker/daemon.json <<EOF
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://docker.1ms.run",
    "https://docker.xuanyuan.me",
    "https://dockerproxy.com"
  ]
}
EOF
echo "重启 Docker 服务以应用配置..."
systemctl restart docker
sleep 3
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker 服务重启失败，请检查配置。"
    exit 1
fi
echo "✅ Docker 镜像加速器配置完成。"

# ---------- 2. 获取宿主机 IP ----------
get_host_ip

# ---------- 3. 部署 MySQL ----------
# 先拉取 MySQL 镜像
pull_image mysql:latest
ensure_mysql
MYSQL_HOST="$HOST_IP"   # 使用宿主机 IP 连接

# 测试外部连接
test_mysql_connection

# ---------- 4. 部署 RocketMQ ----------
echo "=========================================="
echo "部署 RocketMQ (Namesrv: $ROCKETMQ_NAMESRV_PORT, Broker: $ROCKETMQ_BROKER_PORT) ..."
pull_image apache/rocketmq:4.9.4
docker rm -f rocketmq-namesrv rocketmq-broker 2>/dev/null || true

docker run -d --name rocketmq-namesrv \
    -p "$ROCKETMQ_NAMESRV_PORT":9876 \
    -w /home/rocketmq/rocketmq-4.9.4 \
    apache/rocketmq:4.9.4 \
    sh bin/mqnamesrv

docker run -d --name rocketmq-broker \
    -p "$ROCKETMQ_BROKER_PORT":10911 \
    -e "NAMESRV_ADDR=$HOST_IP:$ROCKETMQ_NAMESRV_PORT" \
    -e "BROKER_IP1=$HOST_IP" \
    -w /home/rocketmq/rocketmq-4.9.4 \
    apache/rocketmq:4.9.4 \
    sh bin/mqbroker -n "$HOST_IP:$ROCKETMQ_NAMESRV_PORT" -c conf/broker.conf
echo "✅ RocketMQ 部署完成。"

# ---------- 5. 创建 xxl_job 数据库 ----------
echo "=========================================="
echo "检查并创建 xxl_job 数据库..."
# 拉取 mysql 客户端（已存在则使用缓存）
pull_image mysql:latest

echo "创建 xxl_job 数据库..."
if ! docker run --rm mysql:latest \
    mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" \
    -e "CREATE DATABASE IF NOT EXISTS xxl_job;" >/dev/null 2>&1; then
    echo "❌ 创建数据库失败，请检查权限。"
    # 尝试显示详细错误
    docker run --rm mysql:latest \
        mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" \
        -e "CREATE DATABASE IF NOT EXISTS xxl_job;" 2>&1
    exit 1
fi
echo "✅ 数据库 xxl_job 已就绪。"

# ---------- 6. 部署 xxl-job-admin（端口 8085） ----------
echo "=========================================="
echo "部署 xxl-job-admin (端口 $XXL_JOB_PORT) ..."
pull_image xuxueli/xxl-job-admin:2.4.0
docker rm -f xxl-job-admin 2>/dev/null || true

docker run -d --name xxl-job-admin \
    -p "$XXL_JOB_PORT":8080 \
    -e PARAMS="--spring.datasource.url=jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=$MYSQL_ROOT_PASSWORD" \
    xuxueli/xxl-job-admin:2.4.0
echo "✅ xxl-job-admin 部署完成。"

# ---------- 7. 部署 Nacos（按您要求映射端口） ----------
echo "=========================================="
echo "部署 Nacos (HTTP 端口 $NACOS_PORT，额外 TCP 端口 $NACOS_EXTRA_PORT) ..."
pull_image nacos/nacos-server:latest
docker rm -f nacos 2>/dev/null || true

# 生成 Nacos 鉴权所需的三项环境变量（全部使用随机 Base64 字符串）
NACOS_AUTH_TOKEN=$(openssl rand -base64 32 | tr -d '\n')
NACOS_AUTH_IDENTITY_KEY=$(openssl rand -base64 16 | tr -d '\n')
NACOS_AUTH_IDENTITY_VALUE=$(openssl rand -base64 16 | tr -d '\n')
echo "✅ 已生成 Nacos 认证凭证（Token/Key/Value）"

docker run -d --name nacos \
    -p "$NACOS_PORT":8080 \
    -p "$NACOS_EXTRA_PORT":8848 \
    -p "$NACOS_GPRC":9848 \
    -e MODE=standalone \
    -e NACOS_AUTH_TOKEN="$NACOS_AUTH_TOKEN" \
    -e NACOS_AUTH_IDENTITY_KEY="$NACOS_AUTH_IDENTITY_KEY" \
    -e NACOS_AUTH_IDENTITY_VALUE="$NACOS_AUTH_IDENTITY_VALUE" \
    --restart=unless-stopped \
    nacos/nacos-server:latest
echo "✅ Nacos 部署完成。"

# ---------- 8. 部署 Redis ----------
echo "=========================================="
echo "部署 Redis (端口 $REDIS_PORT) ..."
pull_image redis:latest
docker rm -f redis 2>/dev/null || true

# 构建 Redis 运行命令（若有密码则添加 --requirepass）
REDIS_CMD="redis-server"
if [ -n "$REDIS_PASSWORD" ]; then
    REDIS_CMD="redis-server --requirepass $REDIS_PASSWORD"
fi

docker run -d --name redis \
    -p "$REDIS_PORT":6379 \
    redis:latest \
    $REDIS_CMD
echo "✅ Redis 部署完成。"

# ---------- 9. 打印部署信息 ----------
echo "=========================================="
echo "🎉 所有服务已成功部署！"
echo "------------------------------------------"
echo "访问信息如下（请使用宿主机 IP：$HOST_IP）："
echo "  MySQL        :  $HOST_IP:$MYSQL_PORT (root / $MYSQL_ROOT_PASSWORD)"
echo "  RocketMQ     :  Namesrv: $HOST_IP:$ROCKETMQ_NAMESRV_PORT, Broker: $HOST_IP:$ROCKETMQ_BROKER_PORT"
echo "  xxl-job-admin:  http://$HOST_IP:$XXL_JOB_PORT/xxl-job-admin  (默认账号: admin / 123456)"
echo "  Nacos HTTP   :  http://$HOST_IP:$NACOS_PORT/nacos  (默认账号: nacos / nacos)"
echo "  Nacos 额外TCP:  $HOST_IP:$NACOS_EXTRA_PORT (按您要求保留)"
echo "  Redis        :  redis://$HOST_IP:$REDIS_PORT  (密码: ${REDIS_PASSWORD:-无})"
echo "------------------------------------------"
echo "⚠️  若防火墙开启，请确保以上端口已放行。"
echo "⚠️  MySQL 数据目录：${MYSQL_DATA_DIR:-（匿名卷）}"
echo "=========================================="

trap - ERR
exit 0