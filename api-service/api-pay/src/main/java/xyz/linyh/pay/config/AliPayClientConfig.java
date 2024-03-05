package xyz.linyh.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;

@Configuration
public class AliPayClientConfig {


    @Bean
    public AlipayClient myAliPayClient(){
        return new DefaultAlipayClient(AlipayProperties.gatewayUrl, AlipayProperties.app_id, getPrivateKey(), "json", AlipayProperties.charset, AlipayProperties.alipay_public_key, AlipayProperties.sign_type);
    }

    /**
     * 从文件中获取私钥
     * @return
     */
    private String getPrivateKey() {
        String privateKey = "";
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\alipay\\sandbox\\customDefinitionPrivateKey.txt"))) {
            privateKey = br.readLine();

        } catch (Exception e) {
            System.out.println("获取privateKey出现错误了");

        }

        return privateKey;
    }
}
