# =====================================================================
# fatjar 企业云原生大单体业务管理系统 - Docker 镜像构建文件
# 镜像基础：eclipse-temurin:17-jre（OpenJDK 17 运行时，体积小、安全性高）
# 构建方式：在仓库根目录执行 docker build -t fatjar-startup:1.0.0 -f deployment/dockerfiles/Dockerfile .
# 运行用户：UID=1000（非 root，符合 K8s SecurityContext 最佳实践）
# 暴露端口：8080（业务）、8081（Actuator 管理）、9999（XXL-JOB 执行器）
# =====================================================================

# 基础镜像：Eclipse Temurin OpenJDK 17 JRE（仅运行时，不含编译器）
FROM eclipse-temurin:17-jre

# 镜像维护者标签（已废弃，仅作元数据展示）
LABEL maintainer="fatjar-team <devops@fatjar.com>"
LABEL description="fatjar 企业云原生大单体业务管理系统运行镜像"

# ---------------------------------------------------------------------
# 创建非 root 用户：UID=1000、GID=1000，用户名 fatjar
# 容器内以非 root 运行，符合安全基线
# ---------------------------------------------------------------------
RUN groupadd -g 1000 fatjar \
    && useradd -u 1000 -g fatjar -m -d /app -s /bin/bash fatjar

# ---------------------------------------------------------------------
# 设置工作目录 /app（与 useradd -d /app 一致）
# 后续 COPY 文件相对此目录
# ---------------------------------------------------------------------
WORKDIR /app

# ---------------------------------------------------------------------
# 复制构建产物：startup 模块打出的 fatjar-startup-1.0.0.jar
# 注意：构建上下文为仓库根目录，路径相对仓库根
# 复制到 /app/app.jar（固定名，便于 ENTRYPOINT 引用）
# ---------------------------------------------------------------------
COPY startup/target/fatjar-startup-1.0.0.jar /app/app.jar

# ---------------------------------------------------------------------
# 设置 JVM 时区为东八区，避免日志时间偏差
# 设置 JVM 容器化参数：
#   -XX:+UseContainerSupport：启用容器感知（JDK10+ 默认开启）
#   -XX:MaxRAMPercentage=75.0：限制堆内存占容器内存 75%（避免 OOM）
#   -Djava.security.egd=file:/dev/./urandom：加速 SecureRandom 启动
# ---------------------------------------------------------------------
ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# ---------------------------------------------------------------------
# 切换到非 root 用户 fatjar（UID=1000）
# 后续运行命令均以 fatjar 用户身份执行
# ---------------------------------------------------------------------
USER 1000:1000

# ---------------------------------------------------------------------
# 声明容器暴露端口（仅文档作用，实际端口映射由 docker run -p 控制）
# 8080：业务 HTTP 端口
# 8081：Spring Boot Actuator 管理端口（健康检查/监控）
# 9999：XXL-JOB 执行器端口
# ---------------------------------------------------------------------
EXPOSE 8080 8081 9999

# ---------------------------------------------------------------------
# 健康检查：每 30 秒请求一次 /actuator/health
# 失败 3 次标记 unhealthy（5 分钟超时）
# ---------------------------------------------------------------------
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fs http://localhost:8081/actuator/health || exit 1

# ---------------------------------------------------------------------
# 启动命令：使用 exec 形式，让 java 进程接收到 SIGTERM 信号
# 通过 JAVA_OPTS 环境变量传递 JVM 参数（可在 docker run 时覆盖）
# 通过 SPRING_PROFILES_ACTIVE 环境变量指定激活的 profile
# ---------------------------------------------------------------------
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev} -jar /app/app.jar"]
