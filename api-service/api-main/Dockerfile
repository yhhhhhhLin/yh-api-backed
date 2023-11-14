# 使用官方的 OpenJDK 17 作为基础镜像
FROM openjdk:8-alpine
RUN apk --no-cache add curl

# 设置工作目录
WORKDIR /app

# 复制项目 JAR 文件到镜像中
COPY target/api-main-1.0-SNAPSHOT.jar /app/api-main-1.0-SNAPSHOT.jar

# 暴露应用程序的端口（如果有需要的话）
EXPOSE 7529

# 定义容器启动时执行的命令
CMD ["java", "-jar", "api-main-1.0-SNAPSHOT.jar"]
