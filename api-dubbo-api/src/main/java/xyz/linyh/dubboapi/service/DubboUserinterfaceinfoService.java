package xyz.linyh.dubboapi.service;



import xyz.linyh.ducommon.common.BaseResponse;


/**
* @author lin
* @description 针对表【userinterfaceinfo(用户接口调用次数关系表)】的数据库操作Service
* @createDate 2023-09-11 21:20:10
*/
public interface DubboUserinterfaceinfoService {

    /**
     * 接口调用成功，调用次数减1，总次数加1
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    public BaseResponse invokeOk(Long interfaceInfoId, Long userId);

    /**
     * 判断接口是否有调用次数或是否可以让这个用户调用
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    public Boolean isInvoke(Long interfaceInfoId, Long userId,Integer pointsRequired);

    /**
     * 判断接口是否可以调用和用户是否有调用次数调用某一个接口
     * @param interfaceId
     * @param userId
     * @return
     */
    public Boolean canInvoke(Long interfaceId,Long userId,Integer pointsRequired);

}
