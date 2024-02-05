package xyz.linyh.audit.controller;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.audit.service.impl.ApiInterfaceAuditServiceImpl;

@RestController
public class TestController {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    private ApiInterfaceAuditServiceImpl apiInterfaceAuditService;

    @GetMapping("/send")
    public void test() {
        rocketMqTemplate.convertAndSend("test-topic-1", "Hello World");

        System.out.println("test");
    }

    @GetMapping("/send2")
    public void testSendToGpt() {
//        ApiInterfaceAudit apiInterfaceAudit = new ApiInterfaceAudit();
//        apiInterfaceAudit.setId(1L);
//        apiInterfaceAudit.setApiDescription("全网搜索并获取暴力视频");
//        apiInterfaceAudit.setName("获取暴力视频");
//        apiInterfaceAudit.setUri("/get");
//        apiInterfaceAudit.setHost("localhost:8090");
//        apiInterfaceAudit.setMethod("POST");
//        apiInterfaceAudit.setResponseheader("无");
//        apiInterfaceAudit.setRequestheader("无");
//        apiInterfaceAudit.setRequestparams("无");
//        apiInterfaceAudit.setGetrequestparams("无");
//        apiInterfaceAudit.setUserId(1L);
//        apiInterfaceAudit.setStatus(1);
//        apiInterfaceAudit.setDescription("");
//        apiInterfaceAudit.setUpdatetime(new Date());
//        apiInterfaceAudit.setCreatetime(new Date());
//        apiInterfaceAuditService.sendAuditInterfaceMsgToGpt(apiInterfaceAudit);
////        apiInterfaceAuditService.sendAuditInterfaceMsgToGpt(apiInterfaceAudit);
    }
}
