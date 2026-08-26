#!/usr/bin/env bash
# =====================================================================
# fatjar 企业云原生大单体业务管理系统 - Nacos 配置初始化脚本
# 功能：
#   1. 通过 Nacos OpenAPI 创建三个命名空间：fatjar-dev / fatjar-sit / fatjar-prd
#   2. 将本目录下的 application-*.yml 配置文件导入到对应命名空间
# 命名空间 ID（tenant）严格使用 fatjar-dev/fatjar-sit/fatjar-prd，
# 与 bootstrap.yml 中 spring.cloud.nacos.discovery.namespace 保持一致。
# 执行方式：bash nacos-init.sh
# 依赖：curl、本目录存在 application-common.yml/application-dev.yml 等文件
# =====================================================================

# ---------------------------------------------------------------------
# 可配置参数：Nacos 服务地址（默认本机 8848 端口）
# 覆盖示例：NACOS_ADDR=192.168.1.10:8848 bash nacos-init.sh
# ---------------------------------------------------------------------
# 默认 localhost:8848：本脚本通常在 Nacos 所在宿主机上执行。
# 切勿写死 192.168.3.100（那是 DEV 开发机 IP），在部署服务器上 curl 会全部打空、
# 命名空间创建/配置导入都会"返回空"，看起来像 Nacos 没反应，其实是地址打不通。
NACOS_ADDR="${NACOS_ADDR:-localhost:8848}"

# Nacos 登录账号（启用鉴权时需修改为真实账号）
NACOS_USER="${NACOS_USER:-nacos}"
NACOS_PWD="${NACOS_PWD:-nacos}"

# 脚本所在目录（用于定位同目录的 yml 配置文件，脚本与 yml 必须同目录）
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 配置默认组名（业务配置统一归到 FATJAR_GROUP 组）
DEFAULT_GROUP="FATJAR_GROUP"

# 配置映射：命名空间 ID -> 关联的环境标识（用于查找 yml 文件后缀）
# 数组：每个元素 "namespaceId:envSuffix"
NAMESPACES=(
  "fatjar-dev:dev"
  "fatjar-sit:sit"
  "fatjar-prd:prd"
)

# 命名空间中文名（用于 Nacos 控制台展示）
declare -A NS_DESC=(
  ["fatjar-dev"]="fatjar开发环境"
  ["fatjar-sit"]="fatjar集成测试环境"
  ["fatjar-prd"]="fatjar生产环境"
)

# ---------------------------------------------------------------------
# 工具函数：打印带颜色的日志
#   $1 级别（INFO/WARN/ERROR），$2 消息
# ---------------------------------------------------------------------
log() {
  local level="$1"
  local msg="$2"
  local color=""
  case "$level" in
    INFO)  color="\033[32m" ;;  # 绿色
    WARN)  color="\033[33m" ;;  # 黄色
    ERROR) color="\033[31m" ;;  # 红色
  esac
  echo -e "${color}[$(date '+%Y-%m-%d %H:%M:%S')] [${level}] ${msg}\033[0m"
}

# ---------------------------------------------------------------------
# 工具函数：拼接 Nacos OpenAPI 完整地址
#   $1 API 路径（如 /nacos/v1/console/namespaces）
# 各调用点用 bash 数组组装 curl 参数：accessToken 作为独立 --data-urlencode 字段
# 追加，避免和 type 等字段串味（&accessToken=xxx 塞进 type 值里会被整体 URL 编码）。
# ---------------------------------------------------------------------
nacos_url() {
  echo "http://${NACOS_ADDR}$1"
}

# ---------------------------------------------------------------------
# 步骤 0：预检 Nacos 连通性
# 不可达直接退出 1，避免后续所有 API 都打空、误判成"版本/鉴权问题"。
# ---------------------------------------------------------------------
log INFO "目标 Nacos 地址：http://${NACOS_ADDR}"
log INFO "预检 Nacos 连通性 ..."
health_code=$(curl -s -m 5 -o /dev/null -w "%{http_code}" \
  "$(nacos_url /nacos/v1/console/health/readiness)" 2>/dev/null)
if [ "$health_code" != "200" ]; then
  log ERROR "Nacos 不可达或未就绪（HTTP ${health_code}）"
  log ERROR "排查：1) Nacos 容器是否启动 docker ps | grep nacos"
  log ERROR "      2) 8848 端口是否映射到本机 docker run 加 -p 8848:8848"
  log ERROR "      3) 地址是否正确，覆盖示例：NACOS_ADDR=<host>:8848 bash nacos-init.sh"
  exit 1
fi
log INFO "Nacos 连通正常（HTTP 200）"

# ---------------------------------------------------------------------
# 步骤 1：获取鉴权 token（可选，关闭鉴权时为空）
# ---------------------------------------------------------------------
TOKEN=""
resp=$(curl -s -X POST "$(nacos_url /nacos/v1/auth/login)" \
      -d "username=${NACOS_USER}&password=${NACOS_PWD}")
TOKEN=$(echo "$resp" | grep -o '"accessToken"[^,}]*' | sed 's/.*"accessToken"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/')
if [ -n "$TOKEN" ]; then
  log INFO "Nacos 鉴权 token 获取成功"
else
  log WARN "未获取到 token（Nacos 未开启鉴权或账号错误），继续以无鉴权方式执行"
fi

# ---------------------------------------------------------------------
# 步骤 2：循环创建三个命名空间
# OpenAPI：POST /nacos/v1/console/namespaces
#   参数：customNamespaceId（自定义ID，不可重复）
#         namespaceName（命名空间名）
#         namespaceDesc（描述）
# 注意：Nacos 自带的 public 命名空间不可删除，自定义 ID 不能为 public
# ---------------------------------------------------------------------
for entry in "${NAMESPACES[@]}"; do
  ns_id="${entry%%:*}"           # 提取命名空间ID，如 fatjar-dev
  ns_name="${NS_DESC[$ns_id]}"   # 中文名，如 fatjar开发环境

  log INFO "创建命名空间：${ns_id}（${ns_name}）"

  # 用数组组装参数，accessToken 作为独立字段（仅在鉴权时追加）
  curl_args=( -s -X POST "$(nacos_url /nacos/v1/console/namespaces)"
      --data-urlencode "customNamespaceId=${ns_id}"
      --data-urlencode "namespaceName=${ns_name}"
      --data-urlencode "namespaceDesc=${ns_name}" )
  if [ -n "$TOKEN" ]; then
    curl_args+=( --data-urlencode "accessToken=${TOKEN}" )
  fi
  resp=$(curl "${curl_args[@]}")

  # Nacos 创建成功返回 true，已存在也会返回 true 或提示已存在
  if echo "$resp" | grep -q "true"; then
    log INFO "命名空间 ${ns_id} 创建成功（或已存在）"
  else
    log WARN "命名空间 ${ns_id} 创建返回：${resp:-（空响应，请检查 Nacos 版本/鉴权）}"
  fi
done

# ---------------------------------------------------------------------
# 步骤 3：导入通用配置 application-common.yml 到所有命名空间
# OpenAPI：POST /nacos/v1/cs/configs
#   参数：dataId / group / tenant / content / type
# 注意：通用配置每个命名空间都导入一份，便于环境内引用 profile=common
# ---------------------------------------------------------------------
COMMON_FILE="${SCRIPT_DIR}/application-common.yml"

if [ ! -f "$COMMON_FILE" ]; then
  log ERROR "通用配置文件不存在：${COMMON_FILE}，请检查脚本与 yml 是否同目录"
  exit 1
fi

# 读取配置文件内容
COMMON_CONTENT=$(cat "$COMMON_FILE")

for entry in "${NAMESPACES[@]}"; do
  ns_id="${entry%%:*}"
  log INFO "导入 application-common.yml -> 命名空间 ${ns_id}"

  curl_args=( -s -X POST "$(nacos_url /nacos/v1/cs/configs)"
      --data-urlencode "dataId=application-common.yml"
      --data-urlencode "group=${DEFAULT_GROUP}"
      --data-urlencode "tenant=${ns_id}"
      --data-urlencode "content=${COMMON_CONTENT}"
      --data-urlencode "type=yaml" )
  if [ -n "$TOKEN" ]; then
    curl_args+=( --data-urlencode "accessToken=${TOKEN}" )
  fi
  resp=$(curl "${curl_args[@]}")

  if echo "$resp" | grep -q "true"; then
    log INFO "application-common.yml 导入成功 -> ${ns_id}"
  else
    log ERROR "application-common.yml 导入失败 -> ${ns_id}，返回：${resp:-（空响应）}"
  fi
done

# ---------------------------------------------------------------------
# 步骤 4：导入各环境配置 application-{env}.yml 到对应命名空间
# 注意：环境配置只导入到对应的命名空间
# ---------------------------------------------------------------------
for entry in "${NAMESPACES[@]}"; do
  ns_id="${entry%%:*}"
  ns_env="${entry##*:}"
  env_file="${SCRIPT_DIR}/application-${ns_env}.yml"

  if [ ! -f "$env_file" ]; then
    log WARN "环境配置文件不存在：${env_file}，跳过"
    continue
  fi

  env_content=$(cat "$env_file")
  data_id="application-${ns_env}.yml"

  log INFO "导入 ${data_id} -> 命名空间 ${ns_id}"

  curl_args=( -s -X POST "$(nacos_url /nacos/v1/cs/configs)"
      --data-urlencode "dataId=${data_id}"
      --data-urlencode "group=${DEFAULT_GROUP}"
      --data-urlencode "tenant=${ns_id}"
      --data-urlencode "content=${env_content}"
      --data-urlencode "type=yaml" )
  if [ -n "$TOKEN" ]; then
    curl_args+=( --data-urlencode "accessToken=${TOKEN}" )
  fi
  resp=$(curl "${curl_args[@]}")

  if echo "$resp" | grep -q "true"; then
    log INFO "${data_id} 导入成功 -> ${ns_id}"
  else
    log ERROR "${data_id} 导入失败 -> ${ns_id}，返回：${resp:-（空响应）}"
  fi
done

# ---------------------------------------------------------------------
# 步骤 5：收尾提示
# 实际工程中 bootstrap.yml 多放在 jar 内，按需决定是否上传到 Nacos
# ---------------------------------------------------------------------
log INFO "Nacos 配置初始化完成，请到 Nacos 控制台核对配置"
log INFO "命名空间：fatjar-dev / fatjar-sit / fatjar-prd"
log INFO "DataId：application-common.yml + application-{env}.yml"
log INFO "Group：${DEFAULT_GROUP}"

exit 0
