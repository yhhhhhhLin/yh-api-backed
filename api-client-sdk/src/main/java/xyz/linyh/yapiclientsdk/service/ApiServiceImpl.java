package xyz.linyh.yapiclientsdk.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.common.InterfaceTypeEnum;
import xyz.linyh.ducommon.constant.HeaderNameConstant;
import xyz.linyh.yapiclientsdk.entitys.InterfaceParams;
import xyz.linyh.yapiclientsdk.exception.ClientErrorCode;
import xyz.linyh.yapiclientsdk.exception.ClientException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linzz
 */
@Service
@Slf4j
public class ApiServiceImpl implements ApiService {

//    public static final String GATEWAY_PRE_PATH = InterfaceInfoConstant.GATEWAY_PATH;

    /**
     * 添加api签名认证材料和发送请求到网关
     *
     * @param
     * @return
     */
    public String request(String baseUrl, String uri, String accessKey, String sign, Integer interfaceType, InterfaceParams interfaceParams) {

        if (StrUtil.isBlank(baseUrl)) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "baseUrl不能为空");
        }


        if (interfaceParams == null) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "interfaceParams不能为空");
        }

        HashMap<String, String> headers = addHeader(sign, accessKey, uri, interfaceType);

//        2. 发送到真实请求
//        判断是get请求还是post请求 发送携带参数请求

//        判断是否有请求参数
        Boolean hasParams = false;
        if (interfaceParams != null && interfaceParams.getRequestParams() != null && interfaceParams.getRequestParams().size() > 0) {
            hasParams = true;
        }

//        判断是否有请求体
        Boolean hasBody = false;
        if (interfaceParams != null && interfaceParams.getRequestBody() != null && interfaceParams.getRequestBody().size() > 0) {
            hasBody = true;
        }

//        发送请求
        String body = null;
        Map<String, Object> requestParams = interfaceParams.getRequestParams();

        try {

            HttpResponse response = null;
            if ("GET".equals(interfaceParams.getRequestMethod().toUpperCase())) {
                if (hasParams) {
//                    String res = HttpUtil.get(GATEWAY_PRE_PATH + uri, requestParams);
                    response = HttpRequest.get(baseUrl + uri).form(requestParams)
                            .addHeaders(headers).execute();
                    body = response.body();
                } else {
                    response = HttpRequest.get(baseUrl + uri)
                            .addHeaders(headers).execute();
                    body = response.body();
                }
            } else if ("POST".equals(interfaceParams.getRequestMethod().toUpperCase())) {
                HttpRequest req = HttpRequest.post(baseUrl + uri);
                if (hasParams) {
                    req = req.form(requestParams);
                }
                if (hasBody) {
                    req = req.body(JSONUtil.toJsonStr(interfaceParams.getRequestBody()));
                }
                response = req.addHeaders(headers).execute();
                body = response.body();
            }
            return getReturnMsg(response);

        } catch (HttpException e) {
            log.info("网关发送请求出现异常");
            throw new ClientException(ClientErrorCode.SYSTEM_ERROR);
        }

    }

    private String getReturnMsg(HttpResponse response) {
        if (response == null) {
            return "系统异常";
        }

        BaseResponse baseResponse = JSONUtil.toBean(response.body(), BaseResponse.class);

        if (response.getStatus() != HttpStatus.HTTP_OK) {
            return "系统出错(可能是提供服务的服务器失联):" + response.body();
        }

        try {

            if (baseResponse.getCode() == ErrorCodeEnum.SUCCESS.getCode()) {
                return JSONUtil.toJsonStr(baseResponse.getData());
            } else {
                return baseResponse.getMessage();
            }

        } catch (HttpException e) {
            log.info("发送请求到接口相应值转为实体类出错:{}", e.getMessage());
        }


        return null;
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
    public String request(String baseUrl, String uri, String method, Integer interfaceTypeCode, String accessKey, String sign) {
        if (StrUtil.isBlank(baseUrl)) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "baseUrl不能为空");
        }

        HashMap<String, String> headers = addHeader(sign, accessKey, uri, interfaceTypeCode);
        HttpResponse response = null;
        String body = null;
        try {
            if ("GET".equals(method.toUpperCase())) {
                response = HttpRequest.get(baseUrl + uri)
                        .addHeaders(headers).execute();
                body = response.body();

            } else if ("POST".equals(method.toUpperCase())) {
                response = HttpRequest.post(baseUrl + uri).addHeaders(headers).execute();
                body = response.body();

            } else {
                throw new ClientException(ClientErrorCode.PARAMS_ERROR, "不能支持post和get意外的请求方法");
            }
            return getReturnMsg(response);

        } catch (Exception e) {
            log.info("网关发送请求出现异常");
            throw new ClientException(ClientErrorCode.SYSTEM_ERROR);
        }

    }

    private final String DATABASE_URL = "yhapiBatabase";

    public HashMap<String, String> addHeader(String sign, String accessKey, String uri, Integer interfaceTypeCode) {
        HashMap<String, String> headers = new HashMap<String, String>();
//        1. 添加请求头
//        1.1 添加时间戳
        String timeS = String.valueOf(System.currentTimeMillis() / 1000);
        headers.put("timeS", timeS);
//        如果接口类型是数据源接口，那么转发的地址改为固定的
        if(InterfaceTypeEnum.DATABASE_INTERFACE.getCode().equals(interfaceTypeCode)){
            headers.put("uri", DATABASE_URL);
        }else {
            headers.put("uri", uri);
        }
        headers.put(HeaderNameConstant.INTERFACE_TYPE_HEADER, String.valueOf(interfaceTypeCode));
//        1.2 添加签名认证
        headers.put("sign", sign);
        headers.put("accessKey", accessKey);
        headers.put("randomNum", RandomUtil.randomNumbers(2));
        return headers;
    }
}
