package xyz.linyh.yhapi.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.base.dtos.IdDto;
import xyz.linyh.model.datasource.dtos.AddOrUpdateDscInfoDto;
import xyz.linyh.model.datasource.dtos.ListDscInfoDto;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.datasource.vos.DscInfoVo;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.utils.PageResultUtil;
import xyz.linyh.yhapi.datasource.DataSourceClient;
import xyz.linyh.yhapi.factory.DataSourceClientFactory;
import xyz.linyh.yhapi.mapper.DscInfoMapper;
import xyz.linyh.yhapi.service.DscInfoService;
import xyz.linyh.yhapi.service.InterfaceinfoService;

import java.util.List;

/**
 * @author linzz
 * @description 针对表【dscinfo(数据库连接表)】的数据库操作Service实现
 * @createDate 2024-08-28 23:11:09
 */
@Service
public class DscinfoServiceImpl extends ServiceImpl<DscInfoMapper, DscInfo>
        implements DscInfoService {

    @Autowired
    private InterfaceinfoService interfaceinfoService;

    @Override
    public BaseResponse<Boolean> testConnect(AddOrUpdateDscInfoDto dto) {
        DataSourceClient client = DataSourceClientFactory.getClient(dto.getDscType(), dto.getUrl(), dto.getUsername(), dto.getPassword());
        return ResultUtils.success(client.testConnection());
    }

    @Override
    @Transactional
    public BaseResponse<Boolean> addOrUpdateDataSource(AddOrUpdateDscInfoDto dto) {
        DscInfo dscInfo = new DscInfo();
        BeanUtils.copyProperties(dto, dscInfo);
        String schemaName = getSchemaUrl(dto.getUrl());
        dscInfo.setSchemaName(schemaName);
        boolean result = this.saveOrUpdate(dscInfo);
        return ResultUtils.success(result);
    }

    @Override
    public BaseResponse<Boolean> deleteDataSource(Long dscId) {
//        校验如果已经有api绑定了这个数据源，那么这个数据源不可以删除
        List<Interfaceinfo> interfaceinfos = interfaceinfoService.listByDscId(dscId);
        if (!interfaceinfos.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "数据源已经有绑定的接口，无法删除");
        }

        boolean result = this.removeById(dscId);
        return ResultUtils.success(result);
    }

    @Override
    public BaseResponse<DscInfoVo> getDscInfoById(IdDto dto) {
        Long dscId = dto.getId();
        DscInfo dscInfo = this.getById(dscId);

        DscInfoVo dscInfoVo = new DscInfoVo();
        BeanUtils.copyProperties(dscInfo, dscInfoVo);
        return ResultUtils.success(dscInfoVo);
    }

    @Override
    public BaseResponse<Page<DscInfoVo>> listPage(ListDscInfoDto dto) {
        Integer dscType = dto.getDscType();
        Integer status = dto.getStatus();
        Page<DscInfo> page = lambdaQuery().eq(dscType != null, DscInfo::getDscType, dscType)
                .eq(status != null, DscInfo::getStatus, status)
                .page(new Page<>(dto.getCurrent(), dto.getPageSize()));

        Page<DscInfoVo> result = PageResultUtil.transfer(page, dscInfo -> {
            DscInfoVo dscInfoVo = new DscInfoVo();
            BeanUtils.copyProperties(dscInfo, dscInfoVo);
            return dscInfoVo;
        });

        return ResultUtils.success(result);
    }

    private String getSchemaUrl(String url) {
//        url前面已经校验好格式有schemaName的
        String[] split = url.split("/");
        return split[split.length - 1];
    }
}




