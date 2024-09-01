package xyz.linyh.yhapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;

import java.util.List;

/**
 * @author linzz
 * @description 针对表【dscinterfacecolumn(数据源接口和对应查询表字段关系表)】的数据库操作Service
 * @createDate 2024-08-30 22:28:00
 */
public interface DscInterfaceColumnService extends IService<DscInterfaceColumn> {

    List<DscInterfaceColumn> listByInterfaceInfoIds(List<Long> interfaceInfoIds);

    Boolean saveBatch(List<ColumnBriefVO> searchColumns);
}
