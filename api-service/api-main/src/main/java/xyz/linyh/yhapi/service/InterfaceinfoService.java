package xyz.linyh.yhapi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoInvokeRequest;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoQueryRequest;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lin
 * @description 针对表【interfaceinfo(接口信息表)】的数据库操作Service
 * @createDate 2023-09-03 19:31:19
 */
public interface InterfaceinfoService extends IService<Interfaceinfo> {

    /**
     * 对接口信息进行校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfoParams(Interfaceinfo interfaceInfo, boolean add);



    Interfaceinfo getInterfaceInfoByURI(String interfaceURI, String method);

    /**
     * 刷新网关的接口缓存数据
     *
     * @return
     */
    Boolean updateGatewayCache();

    /**
     * 判断某一个接口是否上线
     *
     * @param interfaceId
     * @return
     */
    Boolean isOnline(Long interfaceId);

    /**
     * 执行某一个接口
     *
     * @param interfaceInfo
     * @param interfaceInfoInvokeRequest
     * @return
     */
    String invokeInterface(User user, Interfaceinfo interfaceInfo, InterfaceInfoInvokeRequest interfaceInfoInvokeRequest);

    /**
     * 添加接口信息
     * @param interfaceInfo
     * @return
     */
    boolean addInterfaceInfo(Interfaceinfo interfaceInfo);

    /**
     * 分页查询所有接口信息
     * @param interfaceInfoQueryRequest
     * @return
     */
    Page<Interfaceinfo> selectInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 更新接口信息
     * @param user
     * @param interfaceInfo
     * @return
     */
    boolean updateInterfaceInfo(User user, Interfaceinfo interfaceInfo);


    /**
     * 刷新gateway中缓存的接口数据
     * @param
     * @param
     * @return
     */
//    Boolean updateGatewayCache();
}
