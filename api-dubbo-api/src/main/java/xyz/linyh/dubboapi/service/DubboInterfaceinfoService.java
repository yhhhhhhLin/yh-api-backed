package xyz.linyh.dubboapi.service;



import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;

import java.util.List;

/**
* @author lin
* @description 针对表【interfaceinfo(接口信息表)】的数据库操作Service
* @createDate 2023-09-03 19:31:19
*/
public interface DubboInterfaceinfoService{

    /**
     * 根据接口地址获取接口信息
     * @param
     * @param
     */
    Interfaceinfo getInterfaceByURI(String uri, String method);

    /**
     * 获取所有可用interface
     * @param
     * @param
     */
    List<Interfaceinfo> getAllInterface();

    /**
     * 添加新的接口到数据库中
     * @param interfaceinfo
     * @return 添加后的接口id
     */
    Long addOrUpdateInterface(Interfaceinfo interfaceinfo);

    /**
     * 修改接口状态
     * @param id
     * @param status
     * @return
     */
    Boolean updateInterfaceStatusById(Long id, Integer status);


    /**
     * 刷新网关的接口缓存数据
     * @return
     */
    Boolean updateGatewayCache();



}
