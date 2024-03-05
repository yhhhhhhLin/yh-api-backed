package xyz.linyh.pay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 */

public class AlipayProperties {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "9021000133635987";
	
    public static String merchant_private_key = "xx";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw62SLpc1e4QmdV3GthkfElT9jcF8sgyWaRQYfLRZKj2XcCZkYS+hKSuDj1aTAsgUFpd3plWtFTbGqvcl8wYmgpVKzMQBQ5b0ptRLXuOR0zcPxtJvC6h1tmp12N2bkA4gzy5UI5ois4eX4DGTjMq7G9AIk8me1QYPic/ivcFzuW77cWdWVdrLCvn8nk2KRxr+BdbIlk+m2k5W4wDBeogrhRZlT0cJZ21p5dz4EPqoaDViEiO9oiO/w2bHN6Jz2VI1dOjGoTzSPcJASZ+Gpi+9TCHkKuflY7pInCVfLAMEBujnm7N7Ib/kENt1ytgnljcd6BFIpln3ZEAr5OGZIRW1ZwIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://mg4whg.natappfree.cc/test/order/url/notify";

	// 支付后跳转到的页面
	public static String return_url = "http://mg4whg.natappfree.cc/test/order/url/return";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
	public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "F:\\AllIdeaProject\\alipay-test\\src\\main\\resources\\log";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

