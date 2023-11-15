
# 使用官方的 OpenJDK 17 作为基础镜像
FROM openjdk:17-alpine

# 设置工作目录
WORKDIR /app

# 安装 curl 工具
RUN apk --no-cache add curl

# 复制项目 JAR 文件到镜像中
COPY ./target/api-test-interface-1.0-SNAPSHOT.jar /app/api-test-interface-1.0-SNAPSHOT.jar



# 暴露应用程序的端口（如果有需要的话）
EXPOSE 7601

CMD ["java", "-jar", "api-test-interface-1.0-SNAPSHOT.jar"]
