package xyz.linyh.yhapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.linyh.model.apitoken.entitys.ApiTokenRel;
import xyz.linyh.yhapi.mapper.ApiTokenRelMapper;
import xyz.linyh.yhapi.service.ApiTokenRelService;

/**
 * @author linzz
 */
@Service
public class ApiTokenRelServiceImpl extends ServiceImpl<ApiTokenRelMapper, ApiTokenRel>
        implements ApiTokenRelService {
}
