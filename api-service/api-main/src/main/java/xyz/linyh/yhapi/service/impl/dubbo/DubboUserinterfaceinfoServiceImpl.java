package xyz.linyh.yhapi.service.impl.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.dubboapi.service.DubboUserinterfaceinfoService;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.model.interfaceinfo.InterfaceInfoInvokePayType;
import xyz.linyh.yhapi.service.InterfaceinfoService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

@DubboService
@Slf4j
public class DubboUserinterfaceinfoServiceImpl implements DubboUserinterfaceinfoService {

    @Autowired
    private UserinterfaceinfoService userinterfaceinfoService;

    @Autowired
    private InterfaceinfoService interfaceinfoService;


    @Override
    public BaseResponse invokeOk(Long interfaceInfoId, Long userId ,InterfaceInfoInvokePayType payType) {
        try {
            return userinterfaceinfoService.invokeOk(interfaceInfoId, userId,payType);
        } catch (Exception e) {
            log.info("DubboUserinterfaceinfoServiceImpl错误");
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断接口是否还有调用次数或是否允许这个用户调用
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public InterfaceInfoInvokePayType isInvoke(Long interfaceInfoId, Long userId, Integer pointsRequired) {
        return userinterfaceinfoService.isInvokeAndGetPayType(interfaceInfoId, userId,pointsRequired);
    }

    /**
     * 判断接口是否可以调用和用户是否有调用次数调用某一个接口
     *
     * @param interfaceId
     * @param userId
     * @return
     */
    @Override
    public InterfaceInfoInvokePayType canInvoke(Long interfaceId, Long userId,Integer pointsRequired) {
        InterfaceInfoInvokePayType payType = userinterfaceinfoService.isInvokeAndGetPayType(interfaceId, userId,pointsRequired);
        Boolean isOnline = interfaceinfoService.isOnline(interfaceId);
//        TODO
//        if (invoke && isOnline) {
//            return true;
//        }
        return payType;
    }
}
