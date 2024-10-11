package xyz.linyh.yapiclientsdk.client;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import xyz.linyh.yapiclientsdk.entitys.InterfaceParams;
import xyz.linyh.yapiclientsdk.exception.ClientErrorCode;
import xyz.linyh.yapiclientsdk.exception.ClientException;
import xyz.linyh.yapiclientsdk.service.ApiServiceImpl;
import xyz.linyh.yapiclientsdk.utils.MyDigestUtils;


/**
 * @author lin
 */
@Data
public class ApiClient {

    private String accessKey;

    private String secretKey;

    private String baseUrl;

    /**
     * 接口类型 --> interfaceTypeEnum
     */
    private Integer interfaceType;

    public ApiClient(String accessKey, String secretKey, String baseUrl) {
        this.accessKey = accessKey;
//        判断baseUrl最后一个是否是/ 如果是/那么就删除
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.baseUrl = baseUrl + "/interface";
        this.secretKey = secretKey;
    }

    private ApiClient() {

    }


    /**
     * sdk通过这个请求发送请求到网关
     * 发送带有请求参数的请求
     *
     * @param uri             请求的uri地址
     * @param interfaceParams 携带请求体和请求参数和请求方法
     * @return
     */
    public String request(String uri, InterfaceParams interfaceParams) {
        ApiServiceImpl apiService = new ApiServiceImpl();
        if (uri == null) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "请求uri不能为空");
        }
        if (StrUtil.isBlank(accessKey) || StrUtil.isBlank(secretKey)) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "用户密钥不能为空");
        }
        if (interfaceParams == null || interfaceParams.getRequestMethod() == null) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "请求参数或请求方法不能为空");
        }

        String sign = MyDigestUtils.getDigest(secretKey);

        return apiService.request(baseUrl, uri, accessKey, sign,interfaceType, interfaceParams);
    }

    /**
     * 发送最简单的请求
     *
     * @param uri
     * @param method
     * @return
     */
    public String request(String uri, String method) {
        if (uri == null) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "请求uri不能为空");
        }
        ApiServiceImpl apiService = new ApiServiceImpl();
        if (StrUtil.isBlank(accessKey) || StrUtil.isBlank(secretKey)) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR);
        }
        if (method == null) {
            throw new ClientException(ClientErrorCode.PARAMS_ERROR, "请求方法不能为空");
        }

        String sign = MyDigestUtils.getDigest(secretKey);

        return apiService.request(baseUrl, uri, method, interfaceType, accessKey, sign);
    }
}
