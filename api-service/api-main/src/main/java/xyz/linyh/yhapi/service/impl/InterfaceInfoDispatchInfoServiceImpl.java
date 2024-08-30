package xyz.linyh.yhapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import xyz.linyh.model.interfaceInfoDispatchInfo.dtos.DispatchInfoDto;
import xyz.linyh.model.interfaceInfoDispatchInfo.entitys.InterfaceInfoDispatchInfo;
import xyz.linyh.yhapi.mapper.InterfaceInfoDispatchInfoMapper;
import xyz.linyh.yhapi.service.InterfaceInfoDispatchInfoService;

/**
 * @author linzz
 * @description 针对表【interfaceinfodispatchinfo(数据源接口调度信息)】的数据库操作Service实现
 * @createDate 2024-08-30 22:33:03
 */
@Service
public class InterfaceInfoDispatchInfoServiceImpl extends ServiceImpl<InterfaceInfoDispatchInfoMapper, InterfaceInfoDispatchInfo>
        implements InterfaceInfoDispatchInfoService {

    @Override
    public Boolean createDispatchInfo(Long interfaceId, DispatchInfoDto dispatchInfoDto) {
        InterfaceInfoDispatchInfo interfaceInfoDispatchInfo = new InterfaceInfoDispatchInfo();
        BeanUtils.copyProperties(dispatchInfoDto, interfaceInfoDispatchInfo);
        interfaceInfoDispatchInfo.setInterfaceInfoId(interfaceId);
        interfaceInfoDispatchInfo.setStatus(1);
        return save(interfaceInfoDispatchInfo);
    }
}




