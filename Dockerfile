
# 使用官方的 OpenJDK 17 作为基础镜像
FROM openjdk:17-alpine

# 设置工作目录
WORKDIR /app

# 安装 curl 工具
RUN apk --no-cache add curl

# 复制项目 JAR 文件到镜像中
COPY ./api-service/api-weather-interface/target/api-weather-interface-1.0-SNAPSHOT.jar /app/api-weather-interface-1.0-SNAPSHOT.jar

COPY ./config/nacos_config.zip /app/nacos_config.zip

COPY ./script/init_nacos.sh /app/init_nacos.sh

# 赋予脚本执行权限
RUN chmod +x /app/init_nacos.sh

# 暴露应用程序的端口（如果有需要的话）
EXPOSE 8085

# 定义容器启动时执行的命令，包括运行初始化脚本和启动应用程序
#CMD ["sh", "./init_nacos.sh", "&&", "java", "-jar", "./api-weather-interface-1.0-SNAPSHOT.jar"]

#CMD sh  './init_nacos.sh && java -jar ./api-weather-interface-1.0-SNAPSHOT.jar'

CMD sh -c 'sh init_nacos.sh && java -jar /app/api-weather-interface-1.0-SNAPSHOT.jar'
#CMD ["java", "-jar", "./api-weather-interface-1.0-SNAPSHOT.jar"]
