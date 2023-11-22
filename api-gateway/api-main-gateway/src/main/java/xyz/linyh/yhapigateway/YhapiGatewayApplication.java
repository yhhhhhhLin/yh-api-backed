package xyz.linyh.yhapigateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//不加载数据库
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDubbo
public class YhapiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(YhapiGatewayApplication.class, args);
    }

}
