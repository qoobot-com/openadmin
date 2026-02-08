FROM centos:7

LABEL maintainer="OpenAdmin Team <team@qoobot.com>"
LABEL description="OpenAdmin Microservice Governance Framework"
LABEL version="10.3.0"

# 设置环境变量
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseVirtualThreads"
ENV SPRING_PROFILES_ACTIVE="docker"
ENV SERVER_PORT=8080

# 创建应用目录
WORKDIR /app

# 复制jar包
COPY target/*.jar app.jar

# 创建非root用户
RUN addgroup --system appgroup &&\
    adduser --system --ingroup appgroup appuser &&\
    chown -R appuser:appgroup /app

USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]