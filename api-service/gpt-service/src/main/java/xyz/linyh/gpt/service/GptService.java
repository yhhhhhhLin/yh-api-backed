package xyz.linyh.gpt.service;

public interface GptService {

    /**
     * 用来发送请求到gpt来认证接口信息是否正确
     * @param auditContext
     * @return
     */
    public Boolean auditApiInterface(String auditContext);
}
