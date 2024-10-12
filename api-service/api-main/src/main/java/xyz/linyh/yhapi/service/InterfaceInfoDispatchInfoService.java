package xyz.linyh.yhapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.interfaceInfoDispatchInfo.dtos.DispatchInfoDto;
import xyz.linyh.model.interfaceInfoDispatchInfo.entitys.InterfaceInfoDispatchInfo;

import java.util.List;

/**
 * @author linzz
 * @description 针对表【interfaceinfodispatchinfo(数据源接口调度信息)】的数据库操作Service
 * @createDate 2024-08-30 22:33:03
 */
public interface InterfaceInfoDispatchInfoService extends IService<InterfaceInfoDispatchInfo> {

    Boolean createDispatchInfo(Long interfaceId, DispatchInfoDto dispatchInfoDto);

    List<InterfaceInfoDispatchInfo> listByInterfaceInfoIds(List<Long> interfaceInfoIds);

    /**
     * 删除接口调度信息和对已经创建的表进行重命名为xxx_delete?
     * @param interfaceId
     * @return
     */
    boolean removeByInterfaceIdAndDscId(Long interfaceId, Long dscId, String schemaName, String tableName);
}
