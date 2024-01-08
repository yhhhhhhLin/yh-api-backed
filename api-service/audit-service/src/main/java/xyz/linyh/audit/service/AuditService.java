package xyz.linyh.audit.service;

import xyz.linyh.model.apiaudit.dto.ListAuditDto;

import java.util.List;

public interface AuditService {

    /**
     * 通用的查询所有审核内容方法
     *
     * @param dto
     * @return
     */
    public List<Object> listAudit(ListAuditDto dto);
}
