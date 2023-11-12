package xyz.linyh.testinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class YhapiInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YhapiInterfaceApplication.class, args);
    }

}
