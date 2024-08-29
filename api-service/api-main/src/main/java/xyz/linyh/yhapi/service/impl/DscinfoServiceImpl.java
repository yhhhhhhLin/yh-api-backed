package xyz.linyh.yhapi.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.datasource.dtos.AddOrUpdateDscInfoDto;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.yhapi.datasource.DataSourceClient;
import xyz.linyh.yhapi.factory.DataSourceClientFactory;
import xyz.linyh.yhapi.mapper.DscInfoMapper;
import xyz.linyh.yhapi.service.DscInfoService;

import java.util.Arrays;

/**
 * @author linzz
 * @description 针对表【dscinfo(数据库连接表)】的数据库操作Service实现
 * @createDate 2024-08-28 23:11:09
 */
@Service
public class DscinfoServiceImpl extends ServiceImpl<DscInfoMapper, DscInfo>
        implements DscInfoService {

    @Override
    public BaseResponse<Boolean> testConnect(AddOrUpdateDscInfoDto dto) {
        DataSourceClient client = DataSourceClientFactory.getClient(dto.getDscType(), dto.getUrl(), dto.getUsername(), dto.getPassword());
        return ResultUtils.success(client.testConnection());
    }

    @Override
    public BaseResponse<Boolean> addDataSource(AddOrUpdateDscInfoDto dto) {
        DscInfo dscInfo = new DscInfo();
        BeanUtils.copyProperties(dto, dscInfo);
        String scheamName = getSchemaUrl(dto.getUrl());
        dscInfo.setSchemaName(scheamName);
        boolean result = this.save(dscInfo);
        return ResultUtils.success(result);
    }

    private String getSchemaUrl(String url) {
//        url前面已经校验好格式有schemaName的
        String[] split = url.split("/");
        return split[split.length - 1];
    }
}




