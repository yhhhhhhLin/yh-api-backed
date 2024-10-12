package xyz.linyh.yhapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.interfaceInfoDispatchInfo.dtos.DispatchInfoDto;
import xyz.linyh.model.interfaceInfoDispatchInfo.entitys.InterfaceInfoDispatchInfo;
import xyz.linyh.yhapi.factory.GenSqlFactory;
import xyz.linyh.yhapi.helper.GenSql;
import xyz.linyh.yhapi.mapper.InterfaceInfoDispatchInfoMapper;
import xyz.linyh.yhapi.service.DscInfoService;
import xyz.linyh.yhapi.service.InterfaceInfoDispatchInfoService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linzz
 * @description 针对表【interfaceinfodispatchinfo(数据源接口调度信息)】的数据库操作Service实现
 * @createDate 2024-08-30 22:33:03
 */
@Service
public class InterfaceInfoDispatchInfoServiceImpl extends ServiceImpl<InterfaceInfoDispatchInfoMapper, InterfaceInfoDispatchInfo>
        implements InterfaceInfoDispatchInfoService {

    @Autowired
    private DscInfoService dscInfoService;

    @Override
    public Boolean createDispatchInfo(Long interfaceId, DispatchInfoDto dispatchInfoDto) {
        InterfaceInfoDispatchInfo interfaceInfoDispatchInfo = new InterfaceInfoDispatchInfo();
        BeanUtils.copyProperties(dispatchInfoDto, interfaceInfoDispatchInfo);
        interfaceInfoDispatchInfo.setInterfaceInfoId(interfaceId);
        interfaceInfoDispatchInfo.setStatus(1);
        return save(interfaceInfoDispatchInfo);
    }

    @Override
    public List<InterfaceInfoDispatchInfo> listByInterfaceInfoIds(List<Long> interfaceInfoIds) {
        if(interfaceInfoIds == null || interfaceInfoIds.isEmpty()){
            return new ArrayList<>(0);
        }

        return lambdaQuery()
                .in(InterfaceInfoDispatchInfo::getInterfaceInfoId, interfaceInfoIds)
                .list();
    }

    @Override
    public boolean removeByInterfaceIdAndDscId(Long interfaceId, Long dscId, String schemaName, String tableName) {
////        TODO 根据表前缀模糊查询，然后一个一个重命名
//        String resultTableName = String.format("%s.%s", schemaName, tableName + GenSql.TABLE_NAME_FIX + "_" + nowDate);
//        DscInfo dscInfo = dscInfoService.getById(dscId);
//        if(dscInfo == null){
//            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "数据源id错误");
//        }
//
//        GenSql gensql = GenSqlFactory.getGensql(dscInfo.getDscType());
//        gensql.renameTable()

        return lambdaUpdate().eq(InterfaceInfoDispatchInfo::getInterfaceInfoId, interfaceId).remove();
    }
}




