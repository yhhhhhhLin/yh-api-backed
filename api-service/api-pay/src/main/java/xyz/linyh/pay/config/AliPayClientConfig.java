package xyz.linyh.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Configuration
@RefreshScope
public class AliPayClientConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    @Value("${pay.alipay.sandbox.appId}")
    public String app_id;

    public String merchant_private_key = "xx";

    @Value("${pay.alipay.sandbox.alipayPublicKey}")
    public String alipay_public_key;

    @Value("${pay.alipay.sandbox.notifyUrl}")
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public String notify_url;

    // 支付后跳转到的页面
    @Value("${pay.alipay.sandbox.returnUrl}")
    public String return_url;

    // 签名方式
    @Value("${pay.alipay.sandbox.signType}")
    public String sign_type;

    // 字符编码格式
    @Value("${pay.alipay.sandbox.charset}")
    public String charset;

    // 支付宝网关
    @Value("${pay.alipay.sandbox.gatewayUrl}")
    public String gatewayUrl;

    // 支付宝网关
    public String log_path = "F:\\AllIdeaProject\\alipay-test\\src\\main\\resources\\log";

    public static String static_return_url;

    public static String static_notify_url;

    public static String static_charset;
    public static String static_sign_type;
    public static String static_alipay_public_key;


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

//    /**
//     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
//     * @param sWord 要写入日志里的文本内容
//     */
//    public static void logResult(String sWord) {
//        FileWriter writer = null;
//        try {
//            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
//            writer.write(sWord);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    @Bean
    public AlipayClient myAliPayClient(){
        setStaticValue();
        return new DefaultAlipayClient(gatewayUrl, app_id, getPrivateKey(), "json", charset, alipay_public_key, sign_type);
    }


    private void setStaticValue() {
        static_alipay_public_key = alipay_public_key;
        static_charset = charset;
        static_sign_type = sign_type;
        static_return_url = return_url;
        static_notify_url = notify_url;
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
