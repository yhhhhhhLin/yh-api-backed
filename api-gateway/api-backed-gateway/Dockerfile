FROM openjdk:8-alpine
# 设置工作目录
WORKDIR /app

# 复制项目 JAR 文件到镜像中
COPY target/api-backed-gateway-1.0-SNAPSHOT.jar /app/api-backed-gateway-1.0-SNAPSHOT.jar

# 暴露应用程序的端口（如果有需要的话）
EXPOSE 9529

# 定义容器启动时执行的命令
CMD ["java", "-jar", "api-backed-gateway-1.0-SNAPSHOT.jar","--spring.profiles.active=prod"]
