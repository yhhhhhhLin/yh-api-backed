package xyz.linyh.yhapi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.model.base.dtos.IdDto;
import xyz.linyh.model.datasource.dtos.AddOrUpdateDscInfoDto;
import xyz.linyh.model.datasource.dtos.ListColumnsDto;
import xyz.linyh.model.datasource.dtos.ListDscInfoDto;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;
import xyz.linyh.model.datasource.vos.DscInfoVo;

import java.util.List;

/**
 * @author linzz
 * @description 针对表【dscinfo(数据库连接表)】的数据库操作Service
 * @createDate 2024-08-28 23:11:09
 */
public interface DscInfoService extends IService<DscInfo> {

    /**
     * 测试数据源是否可以连通
     *
     * @param dto
     * @return
     */
    BaseResponse<Boolean> testConnect(AddOrUpdateDscInfoDto dto);

    BaseResponse<Boolean> addOrUpdateDataSource(AddOrUpdateDscInfoDto dto);

    BaseResponse<Boolean> deleteDataSource(Long dscId);

    BaseResponse<DscInfoVo> getDscInfoById(IdDto dto);

    BaseResponse<Page<DscInfoVo>> listPage(ListDscInfoDto dto);

    /**
     * 获取对应表的所有字段
     * @param dto
     * @return
     */
    List<ColumnBriefVO> listColumns(ListColumnsDto dto);
}
