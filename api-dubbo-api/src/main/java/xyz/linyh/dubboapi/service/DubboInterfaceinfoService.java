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



}
