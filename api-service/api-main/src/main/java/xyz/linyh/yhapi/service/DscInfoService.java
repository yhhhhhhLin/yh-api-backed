package xyz.linyh.yhapi.service;

import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.model.datasource.dtos.AddOrUpdateDscInfoDto;
import xyz.linyh.model.datasource.entitys.DscInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author linzz
* @description 针对表【dscinfo(数据库连接表)】的数据库操作Service
* @createDate 2024-08-28 23:11:09
*/
public interface DscInfoService extends IService<DscInfo> {

    /**
     * 测试数据源是否可以连通
     * @param dto
     * @return
     */
    BaseResponse<Boolean> testConnect(AddOrUpdateDscInfoDto dto);

    BaseResponse<Boolean> addDataSource(AddOrUpdateDscInfoDto dto);
}
