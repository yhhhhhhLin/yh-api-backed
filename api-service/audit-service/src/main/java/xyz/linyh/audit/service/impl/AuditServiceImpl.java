package xyz.linyh.audit.service.impl;

import org.springframework.stereotype.Service;
import xyz.linyh.audit.service.AuditService;
import xyz.linyh.model.apiaudit.dto.ListAuditDto;

import java.util.List;

/**
 * 关于待审核接口的相关操作
 *
 * @author lin
 */
@Service
public class AuditServiceImpl implements AuditService {


    /**
     * 通用的查询所有审核内容方法
     *
     * @param dto
     * @return
     */
    @Override
    public List<Object> listAudit(ListAuditDto dto) {

        return null;
    }
}
