package xyz.linyh.yhapi.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.yapiclientsdk.client.ApiClient;
import xyz.linyh.yapiclientsdk.config.ApiClientConfig;
import xyz.linyh.yapiclientsdk.service.ApiServiceImpl;
import xyz.linyh.yhapi.mapper.InterfaceinfoMapper;
import xyz.linyh.yhapi.service.InterfaceinfoService;

/**
* @author lin
* @description 针对表【interfaceinfo(接口信息表)】的数据库操作Service实现
* @createDate 2023-09-03 19:31:19
*/
@Service
@Slf4j
public class InterfaceinfoServiceImpl extends ServiceImpl<InterfaceinfoMapper, Interfaceinfo>
    implements InterfaceinfoService{


    /**
     * 对接口信息进行校验
     *
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfo(Interfaceinfo interfaceInfo, boolean add) {

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = interfaceInfo.getName();
        String method = interfaceInfo.getMethod();
        String description = interfaceInfo.getDescription();
        String uri = interfaceInfo.getUri();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, method, description, uri, requestHeader,responseHeader)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 255) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }

    }

    @Override
    public Interfaceinfo getInterfaceInfoByURI(String interfaceURI,String method) {
        if(interfaceURI==null || method==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceinfo = this.getOne(Wrappers.<Interfaceinfo>lambdaQuery()
                .eq(Interfaceinfo::getUri, interfaceURI)
                .eq(Interfaceinfo::getMethod,method)
        );
        return interfaceinfo;
    }

    /**
     * 刷新网关的接口缓存数据
     *
     * @return
     */
    @Autowired
    private ApiClient apiClient;
    @Override
    public Boolean updateGatewayCache() {
        String baseUrl = apiClient.getBaseUrl();
        if(baseUrl==null){
            return false;
        }
//        将最后的地址后面的/interface去掉
        if(baseUrl.endsWith("/interface")){
            baseUrl = baseUrl.replace("/interface","");
        }
//      TODO  后面改为常量
        HttpRequest.get(baseUrl+"/yhapi/routes");
        return true;
    }

//    /**
//     * 刷新gateway中缓存的接口数据
//     *
//     * @param
//     * @param
//     * @return
//     */
//    @Async
//    @Override
//    public Boolean updateGatewayCache() {
//        try {
//            HttpResponse execute = HttpRequest.get(apiClientConfig.getUrl()).execute();
//        } catch (Exception e) {
//            log.error("刷新路由接口数据失败....");
//            throw new RuntimeException(e);
//        }
//        log.info("刷新路由接口数据成功....");
//        return true;
//    }

}




