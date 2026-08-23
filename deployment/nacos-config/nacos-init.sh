#!/usr/bin/env bash
# =====================================================================
# fatjar 企业云原生大单体业务管理系统 - Nacos 配置初始化脚本
# 功能：
#   1. 通过 Nacos OpenAPI 创建三个命名空间：fatjar-dev / fatjar-sit / fatjar-prd
#   2. 将本目录下的 application-*.yml 配置文件导入到对应命名空间
# 命名空间 ID（tenant）严格使用 fatjar-dev/fatjar-sit/fatjar-prd，
#   与 bootstrap.yml 中 spring.cloud.nacos.discovery.namespace 一致
# 执行方式：bash nacos-init.sh
# 依赖：curl、本目录存在 application-common.yml/application-dev.yml 等文件
# =====================================================================

# ---------------------------------------------------------------------
# 可配置参数：Nacos 服务地址（默认本地 8848 端口）
# 可通过环境变量覆盖：NACOS_ADDR=192.168.1.10:8848 bash nacos-init.sh
# ---------------------------------------------------------------------
NACOS_ADDR="${NACOS_ADDR:-localhost:8848}"

# Nacos 默认登录账号（如启用鉴权需修改）
NACOS_USER="${NACOS_USER:-nacos}"
NACOS_PWD="${NACOS_PWD:-nacos}"

# 脚本所在目录（用于定位 yml 配置文件）
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
# 工具函数：登录 Nacos 获取 accessToken（如开启鉴权）
# 关闭鉴权可注释掉此函数调用
# ---------------------------------------------------------------------
get_nacos_token() {
  local resp
  resp=$(curl -s -X POST "http://${NACOS_ADDR}/nacos/v1/auth/login" \
        -d "username=${NACOS_USER}&password=${NACOS_PWD}")
  # 用 grep + sed 提取 accessToken 字段（不依赖 jq）
  echo "$resp" | grep -o '"accessToken"[^,}]*' | sed 's/.*"accessToken"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/'
}

# ---------------------------------------------------------------------
# 步骤 1：获取鉴权 token（可选）
# ---------------------------------------------------------------------
TOKEN=""
TOKEN=$(get_nacos_token)
if [ -n "$TOKEN" ]; then
  log INFO "Nacos 鉴权 token 获取成功"
  TOKEN_PARAM="&accessToken=${TOKEN}"
else
  log WARN "未获取到 token（Nacos 未开启鉴权或账号错误），继续以无鉴权方式执行"
  TOKEN_PARAM=""
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
  ns_env="${entry##*:}"          # 提取环境后缀，如 dev
  ns_name="${NS_DESC[$ns_id]}"   # 中文名，如 fatjar开发环境

  log INFO "创建命名空间：${ns_id}（${ns_name}）"

  resp=$(curl -s -X POST "http://${NACOS_ADDR}/nacos/v1/console/namespaces" \
        -d "customNamespaceId=${ns_id}&namespaceName=${ns_name}&namespaceDesc=${ns_name}${TOKEN_PARAM}")

  # Nacos 创建成功返回 true，已存在也会返回 true 或提示已存在
  if echo "$resp" | grep -q "true"; then
    log INFO "命名空间 ${ns_id} 创建成功（或已存在）"
  else
    log WARN "命名空间 ${ns_id} 创建返回：${resp}"
  fi
done

# ---------------------------------------------------------------------
# 步骤 3：导入通用配置 application-common.yml 到所有命名空间
# OpenAPI：POST /nacos/v1/cs/configs
#   参数：dataId（配置ID）
#         group（分组）
#         tenant（命名空间ID）
#         content（配置内容，需 URL 编码）
#         type（配置类型：yaml/properties/json）
# 注意：通用配置每个命名空间都导入一份，便于环境内引用 profile=common
# ---------------------------------------------------------------------
COMMON_FILE="${SCRIPT_DIR}/application-common.yml"

if [ ! -f "$COMMON_FILE" ]; then
  log ERROR "通用配置文件不存在：${COMMON_FILE}，请检查目录"
  exit 1
fi

# 读取配置文件内容
COMMON_CONTENT=$(cat "$COMMON_FILE")

for entry in "${NAMESPACES[@]}"; do
  ns_id="${entry%%:*}"
  log INFO "导入 application-common.yml -> 命名空间 ${ns_id}"

  resp=$(curl -s -X POST "http://${NACOS_ADDR}/nacos/v1/cs/configs" \
        --data-urlencode "dataId=application-common.yml" \
        --data-urlencode "group=${DEFAULT_GROUP}" \
        --data-urlencode "tenant=${ns_id}" \
        --data-urlencode "content=${COMMON_CONTENT}" \
        --data-urlencode "type=yaml${TOKEN_PARAM}")

  if echo "$resp" | grep -q "true"; then
    log INFO "application-common.yml 导入成功 -> ${ns_id}"
  else
    log ERROR "application-common.yml 导入失败 -> ${ns_id}，返回：${resp}"
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

  resp=$(curl -s -X POST "http://${NACOS_ADDR}/nacos/v1/cs/configs" \
        --data-urlencode "dataId=${data_id}" \
        --data-urlencode "group=${DEFAULT_GROUP}" \
        --data-urlencode "tenant=${ns_id}" \
        --data-urlencode "content=${env_content}" \
        --data-urlencode "type=yaml${TOKEN_PARAM}")

  if echo "$resp" | grep -q "true"; then
    log INFO "${data_id} 导入成功 -> ${ns_id}"
  else
    log ERROR "${data_id} 导入失败 -> ${ns_id}，返回：${resp}"
  fi
done

# ---------------------------------------------------------------------
# 步骤 5：导入启动配置 bootstrap.yml 到所有命名空间（可选）
# 这里以命名空间内一份 bootstrap.yml 为例，便于不同环境隔离
# 实际工程中 bootstrap.yml 多放在 jar 内，按需决定是否上传
# ---------------------------------------------------------------------
log INFO "Nacos 配置初始化完成，请到 Nacos 控制台核对配置"
log INFO "命名空间：fatjar-dev / fatjar-sit / fatjar-prd"
log INFO "DataId：application-common.yml + application-{env}.yml"
log INFO "Group：${DEFAULT_GROUP}"

exit 0
