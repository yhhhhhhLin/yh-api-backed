# 使用官方的 OpenJDK 17 作为基础镜像
FROM openjdk:17-alpine

# 设置工作目录
WORKDIR /app

# 复制项目 JAR 文件到镜像中
COPY target/api-weather-interface-1.0-SNAPSHOT.jar /app/api-weather-interface-1.0-SNAPSHOT.jar

# 暴露应用程序的端口（如果有需要的话）
EXPOSE 8085

# 定义容器启动时执行的命令
CMD ["java", "-jar", "api-weather-interface-1.0-SNAPSHOT.jar"]
