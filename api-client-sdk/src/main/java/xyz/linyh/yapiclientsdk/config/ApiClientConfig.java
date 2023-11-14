package xyz.linyh.yapiclientsdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import xyz.linyh.yapiclientsdk.client.ApiClient;

@Configuration
@ConfigurationProperties("yhapi.client")
@Data
@ComponentScan()
public class ApiClientConfig {

    private String accessKey;

    private String secretKey;

    private String url;

    @Bean
    public ApiClient apiClient(){
        return new ApiClient(accessKey, secretKey);
    }


}
