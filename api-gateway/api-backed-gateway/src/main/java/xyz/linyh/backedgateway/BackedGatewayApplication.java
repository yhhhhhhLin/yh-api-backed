package xyz.linyh.backedgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BackedGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackedGatewayApplication.class,args);
    }
}
