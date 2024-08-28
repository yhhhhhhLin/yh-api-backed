package xyz.linyh.yhapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.yhapi.mapper.DscInfoMapper;
import xyz.linyh.yhapi.service.DscInfoService;

/**
 * @author linzz
 * @description 针对表【dscinfo(数据库连接表)】的数据库操作Service实现
 * @createDate 2024-08-28 23:11:09
 */
@Service
public class DscinfoServiceImpl extends ServiceImpl<DscInfoMapper, DscInfo>
        implements DscInfoService {

}




