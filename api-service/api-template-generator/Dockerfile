FROM openjdk:17-alpine
WORKDIR /app


COPY ./template /app/template

COPY target/api-template-generator-1.0-SNAPSHOT.jar /app/api-template-generator-1.0-SNAPSHOT.jar
RUN apk --no-cache add curl
RUN apk --no-cache add iputils

# 暴露应用程序的端口（如果有需要的话）
EXPOSE 7533

# 定义容器启动时执行的命令
CMD ["java","-Xms150m","-Xmx180m", "-jar", "api-template-generator-1.0-SNAPSHOT.jar","--spring.profiles.active=prod"]
