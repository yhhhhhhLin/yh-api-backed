package xyz.linyh.yhapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;
import xyz.linyh.yhapi.mapper.DscInterfaceColumnMapper;
import xyz.linyh.yhapi.service.DscInterfaceColumnService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linzz
 * @description 针对表【dscinterfacecolumn(数据源接口和对应查询表字段关系表)】的数据库操作Service实现
 * @createDate 2024-08-30 22:28:00
 */
@Service
public class DscInterfaceColumnServiceImpl extends ServiceImpl<DscInterfaceColumnMapper, DscInterfaceColumn>
        implements DscInterfaceColumnService {

    @Override
    public List<DscInterfaceColumn> listByInterfaceInfoIds(List<Long> interfaceInfoIds) {
        if(interfaceInfoIds == null || interfaceInfoIds.isEmpty()){
            return new ArrayList<>(0);
        }

        return lambdaQuery()
                .in(DscInterfaceColumn::getInterfaceInfoId, interfaceInfoIds)
                .list();
    }

    @Override
    @Transactional
    public Boolean saveBatch(List<ColumnBriefVO> searchColumns) {
        List<DscInterfaceColumn> dscInterfaceColumns = convertToDscInterfaceColumns(searchColumns);
        return saveBatch(dscInterfaceColumns);
    }

    @Override
    public boolean removeBatchByInterfaceId(Long interfaceId) {
        return lambdaUpdate()
                .eq(DscInterfaceColumn::getInterfaceInfoId, interfaceId)
                .remove();
    }

    private List<DscInterfaceColumn> convertToDscInterfaceColumns(List<ColumnBriefVO> searchColumns) {
        List<DscInterfaceColumn> dscInterfaceColumns = new ArrayList<>();
        searchColumns.forEach(columnBriefVO -> {
            DscInterfaceColumn dscInterfaceColumn = new DscInterfaceColumn();
            BeanUtils.copyProperties(columnBriefVO, dscInterfaceColumn);
            dscInterfaceColumns.add(dscInterfaceColumn);
        });
        return dscInterfaceColumns;
    }
}




