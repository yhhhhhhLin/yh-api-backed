package xyz.linyh.yapiclientsdk.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.ducommon.requestParms.InterfaceParams;
import xyz.linyh.yapiclientsdk.service.ApiService;
import xyz.linyh.yapiclientsdk.service.ApiServiceImpl;
import xyz.linyh.yapiclientsdk.utils.MyDigestUtils;
/**
 * @author lin
 */
@Component
@Data
public class ApiClient {




    private String accessKey;

    private String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public ApiClient() {

    }


    /**
     * sdk通过这个请求发送请求到网关
     * 发送带有请求参数的请求
     * @param uri 请求的uri地址
     * @param interfaceParams 携带请求体和请求参数和请求方法
     * @return
     */
    public String request(String uri, InterfaceParams interfaceParams){
        ApiServiceImpl apiService = new ApiServiceImpl();
        if(uri==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求uri不能为空");
        }
        if(StrUtil.isBlank(accessKey) || StrUtil.isBlank(secretKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密钥不能为空");
        }
        if(interfaceParams==null || interfaceParams.getRequestMethod()==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数或请求方法不能为空");
        }

        String sign = MyDigestUtils.getDigest(secretKey);

        return apiService.request(uri,accessKey,sign,interfaceParams);
    }

    /**
     * 发送最简单的请求
     * @param uri
     * @param method
     * @return
     */
    public String request(String uri,String method){
        if(uri==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求uri不能为空");
        }
        ApiServiceImpl apiService = new ApiServiceImpl();
        if(StrUtil.isBlank(accessKey) || StrUtil.isBlank(secretKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(method==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求方法不能为空");
        }

        String sign = MyDigestUtils.getDigest(secretKey);

        return apiService.request(uri,method,accessKey,sign);
    }
}
