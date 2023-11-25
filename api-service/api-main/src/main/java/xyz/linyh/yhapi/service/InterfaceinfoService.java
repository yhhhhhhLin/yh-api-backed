package xyz.linyh.yhapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;

/**
* @author lin
* @description 针对表【interfaceinfo(接口信息表)】的数据库操作Service
* @createDate 2023-09-03 19:31:19
*/
public interface InterfaceinfoService extends IService<Interfaceinfo> {

    /**
     * 对接口信息进行校验
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(Interfaceinfo interfaceInfo, boolean add);

    Interfaceinfo getInterfaceInfoByURI(String interfaceURI,String method);

    /**
     * 刷新网关的接口缓存数据
     * @return
     */
    Boolean updateGatewayCache();

    /**
     * 刷新gateway中缓存的接口数据
     * @param
     * @param
     * @return
     */
//    Boolean updateGatewayCache();
}
