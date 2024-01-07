package xyz.linyh.yhapi.service;

import xyz.linyh.model.interfaceinfo.dto.GRequestParamsDto;
import xyz.linyh.model.interfaceinfo.dto.RequestHeaderParamsDto;

import java.util.List;

public interface InterfaceDebugService {

    /**
     * 执行调试接口
     *
     * @return
     */
    String invokeDebug(List<GRequestParamsDto> getRequestParams, List<RequestHeaderParamsDto> requestHeaderParamsDtos, String preUrl, String suffUrl, String method, String postValue);
}
