package xyz.linyh.yapiclientsdk.service;

import xyz.linyh.yapiclientsdk.entitys.InterfaceParams;

public interface ApiService {


    /**
     * 添加api签名认证材料和发送请求到网关（发送携带请求参数的）
     *
     * @param uri
     * @param sign
     * @return
     */
    String request(String baseUrl, String uri, String accessKey, String sign, InterfaceParams interfaceParams);

    /**
     * 发送没有携带请求参数的请求
     *
     * @param uri
     * @param method
     * @param accessKey
     * @param sign
     * @return
     */
    String request(String baseUrl, String uri, String method, Integer interfaceType, String accessKey, String sign);
}
