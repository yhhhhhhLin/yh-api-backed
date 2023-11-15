package xyz.linyh.yapiclientsdk.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.ducommon.requestParms.InterfaceParams;

import java.util.HashMap;
import java.util.Map;

@Service
//@Slf4j
@Slf4j
public class ApiServiceImpl implements ApiService{

//    @Value("${gateway.url}")
//    public static String GATEWAY_PRE_PATH = "http://gateway:8081/interface";

    public static final String GATEWAY_PRE_PATH = InterfaceInfoConstant.url;

    /**
     * 添加api签名认证材料和发送请求到网关
     *
     * @param uri
     * @return
     */
    public String request(String uri, String accessKey, String sign, InterfaceParams interfaceParams) {
        if(interfaceParams==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"interfaceParams不能为空");
        }

        HashMap<String, String> headers = addHeader(sign, accessKey,uri);

//        2. 发送到真实请求
//        判断是get请求还是post请求 发送携带参数请求

//        判断是否有请求参数
        Boolean hasParams = false;
        if(interfaceParams!=null && interfaceParams.getRequestParams()!=null && interfaceParams.getRequestParams().size()>0){
            hasParams=true;
        }

//        判断是否有请求体
        Boolean hasBody = false;
        if(interfaceParams!=null && interfaceParams.getRequestBody()!=null &&interfaceParams.getRequestBody().size()>0){
            hasBody=true;
        }

//        发送请求
        String body = null;
        Map<String, Object> requestParams = interfaceParams.getRequestParams();

        try {
            if("GET".equals(interfaceParams.getRequestMethod().toUpperCase())){
                if(hasParams){
//                    String res = HttpUtil.get(GATEWAY_PRE_PATH + uri, requestParams);
                    HttpResponse response = HttpRequest.get(GATEWAY_PRE_PATH+uri).form(requestParams)
                            .addHeaders(headers).execute();
                    body = response.body();
                }else{
                    HttpResponse response = HttpRequest.get(GATEWAY_PRE_PATH+uri)
                            .addHeaders(headers).execute();
                    body = response.body();
                }
            }else if("POST".equals(interfaceParams.getRequestMethod().toUpperCase())){
                HttpRequest req = HttpRequest.post(GATEWAY_PRE_PATH + uri);
                if(hasParams){
                    req = req.form(requestParams);
                }
                if(hasBody){
                    req = req.body(JSONUtil.toJsonStr(interfaceParams.getRequestBody()));
                }
                HttpResponse response = req.addHeaders(headers).execute();
                body=response.body();
            }
            return body;
        } catch (HttpException e) {
            log.info("网关发送请求出现异常,网关地址为:{},发送接口地址为{}",GATEWAY_PRE_PATH,uri);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

    }

    /**
     * 发送没有携带请求参数的请求
     *
     * @param uri
     * @param method
     * @param accessKey
     * @param sign
     * @return
     */
    public String request(String uri,String method, String accessKey, String sign) {
        HashMap<String, String> headers = addHeader(sign, accessKey,uri);

        System.out.println("------网关地址为---------"+ GATEWAY_PRE_PATH);;
        String body = null;
        try {
            if("GET".equals(method.toUpperCase())){
                HttpResponse response = HttpRequest.get(GATEWAY_PRE_PATH+uri)
                        .addHeaders(headers).execute();
                body = response.body();

            }else if("POST".equals(method.toUpperCase())){
                HttpResponse response = HttpRequest.post(GATEWAY_PRE_PATH + uri).addHeaders(headers).execute();
                body = response.body();

            }else{
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能支持post和get意外的请求方法");
            }
            return body;

        } catch (Exception e) {
            log.info("网关发送请求出现异常,网关地址为:{},发送接口地址为{}",GATEWAY_PRE_PATH,uri);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

    }

    public HashMap<String, String> addHeader(String sign,String accessKey,String uri){
        HashMap<String, String> headers = new HashMap<String, String>();
//        1. 添加请求头
//        1.1 添加时间戳
        String timeS = String.valueOf(System.currentTimeMillis()/1000);
        headers.put("timeS",timeS);
        headers.put("uri",uri);
//        1.2 添加签名认证
        headers.put("sign",sign);
        headers.put("accessKey", accessKey);
        headers.put("randomNum", RandomUtil.randomNumbers(2));
        return headers;
    }
}
