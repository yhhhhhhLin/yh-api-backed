FROM openjdk:17-alpine
# 设置工作目录
WORKDIR /app

# 复制项目 JAR 文件到镜像中
COPY target/gpt-service-1.0-SNAPSHOT.jar /app/gpt-service-1.0-SNAPSHOT.jar
RUN apk --no-cache add curl
RUN apk --no-cache add iputils

# 暴露应用程序的端口（如果有需要的话）
EXPOSE 7531

# 定义容器启动时执行的命令
CMD ["java", "-jar", "gpt-service-1.0-SNAPSHOT.jar","--spring.profiles.active=prod"]