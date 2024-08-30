package xyz.linyh.yhapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.interfaceInfoDispatchInfo.dtos.DispatchInfoDto;
import xyz.linyh.model.interfaceInfoDispatchInfo.entitys.InterfaceInfoDispatchInfo;

/**
 * @author linzz
 * @description 针对表【interfaceinfodispatchinfo(数据源接口调度信息)】的数据库操作Service
 * @createDate 2024-08-30 22:33:03
 */
public interface InterfaceInfoDispatchInfoService extends IService<InterfaceInfoDispatchInfo> {

    Boolean createDispatchInfo(Long interfaceId, DispatchInfoDto dispatchInfoDto);
}
