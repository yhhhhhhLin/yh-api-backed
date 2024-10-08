package xyz.linyh.yhapi.service.impl.dubbo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.dubboapi.service.DubboInterfaceinfoService;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.model.interfaceinfo.dto.UpdateStatusDto;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.service.InterfaceinfoService;

import java.util.List;

@DubboService
public class DubboInterfaceinfoServiceImpl implements DubboInterfaceinfoService {

    @Autowired
    private InterfaceinfoService interfaceinfoService;

    @Override
    public Interfaceinfo getInterfaceByURI(String interfaceURI, String method) {
        return interfaceinfoService.getInterfaceInfoByURI(interfaceURI, method);
    }

    /**
     * 获取所有可用接口
     *
     * @return
     */
    @Override
    public List<Interfaceinfo> getAllInterface() {
        List<Interfaceinfo> interfaceInfos = interfaceinfoService.list(Wrappers.<Interfaceinfo>lambdaQuery().eq(Interfaceinfo::getStatus, InterfaceInfoConstant.STATIC_USE));
        return interfaceInfos;
    }

    /**
     * 添加新的接口到数据库中
     *
     * @param interfaceinfo
     * @return 添加后的接口id
     */
    @Override
    public Long addOrUpdateInterface(Interfaceinfo interfaceinfo) {
//        判断uri不能重复
        List<Interfaceinfo> dbInterfaceInfos = interfaceinfoService.list(Wrappers.<Interfaceinfo>lambdaQuery()
                .eq(Interfaceinfo::getUri, interfaceinfo.getUri())
                .ne(Interfaceinfo::getId, interfaceinfo.getId()));
        if (dbInterfaceInfos != null && !dbInterfaceInfos.isEmpty()) {
            return null;
        }

        interfaceinfo = interfaceinfoService.saveOrUpdateInterface(interfaceinfo);
        interfaceinfoService.updateGatewayCache();
        if(interfaceinfo!=null) {
            return interfaceinfo.getId();
        }else{
            return null;
        }
    }

    @Override
    public Boolean updateInterfaceStatusById(Long id, Integer status, Long userId) {
        UpdateStatusDto dto = new UpdateStatusDto(id, status);
        User user = new User();
        user.setId(userId);
        return interfaceinfoService.updateInterfaceInfoStatus(dto, user);
    }

    /**
     * 刷新网关的接口缓存数据
     *
     * @return
     */
    @Override
    public Boolean updateGatewayCache() {
        interfaceinfoService.updateGatewayCache();
        return true;
    }

    @Override
    public Interfaceinfo queryInterfaceByURIAndUserId(String uri, Long userId) {
        return interfaceinfoService.getInterfaceInfoByURIAndUserId(uri, userId);
    }

}
