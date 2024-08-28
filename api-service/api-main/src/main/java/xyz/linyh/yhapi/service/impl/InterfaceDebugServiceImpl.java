package xyz.linyh.yhapi.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.dto.GRequestParamsDto;
import xyz.linyh.model.interfaceinfo.dto.RequestHeaderParamsDto;
import xyz.linyh.yhapi.service.InterfaceDebugService;

import java.util.List;

/**
 * @author lin
 */
@Slf4j
@Service
public class InterfaceDebugServiceImpl implements InterfaceDebugService {


    /**
     * 执行调试接口
     *
     * @param getRequestParams
     * @param requestHeaderParamsDtos
     * @param preUrl
     * @param suffUrl
     * @param method
     * @param postValue
     * @return
     */
    @Override
    public String invokeDebug(List<GRequestParamsDto> getRequestParams, List<RequestHeaderParamsDto> requestHeaderParamsDtos, String preUrl, String suffUrl, String method, String postValue) {

//       参数校验
        if (preUrl == null || method == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "需要url前缀和请求方式");
        }


        HttpRequest httpRequest = null;
        if ("GET".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.get(preUrl + suffUrl);
        } else if ("POST".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.post(preUrl + suffUrl);
        } else {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "请求方式错误");
        }

//        添加请求参数
        if (getRequestParams != null && !getRequestParams.isEmpty()) {
            for (GRequestParamsDto getRequestParam : getRequestParams) {
                httpRequest.form(getRequestParam.getRequestParmK(), getRequestParam.getRequestParmV());
            }
        }

//        添加请求头
        if (requestHeaderParamsDtos != null && !requestHeaderParamsDtos.isEmpty()) {
            for (RequestHeaderParamsDto requestHeaderParamsDto : requestHeaderParamsDtos) {
                httpRequest.header(requestHeaderParamsDto.getHeaderKey(), requestHeaderParamsDto.getHeaderValue());
            }
        }

//        添加请求体
        if (postValue != null && "POST".equalsIgnoreCase(method)) {
            httpRequest.body(postValue);
        }

        HttpResponse response = httpRequest.execute();
        log.info("返回结果为:{}", response.body());

        int status = response.getStatus();
        if (status != 200) {
            log.info("接口请求失败:{}", preUrl + suffUrl);
            return response.body();
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求失败");
        }

        log.info("接口请求成功:{}，返回数据为:{}", preUrl + suffUrl, response.body());

        return response.body();

    }


}
