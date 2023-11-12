package xyz.linyh.yhapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.interfaceinfo.vo.InterfaceInfoVO;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.service.UserService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lin
 */
@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    @Resource
    private UserinterfaceinfoService userinterfaceinfoService;

    @Resource
    private UserService userService;


    /**
     * 获取接口调用次数前5的接口
     * @return
     */
    @RequestMapping
    public BaseResponse<List<InterfaceInfoVO>> analyzeInterfaceInfo(){
        return userinterfaceinfoService.analyzeInterfaceInfo();

    }

    /**
     * 分析自己发布的调用次数前5的接口
     * @param request
     * @return
     */
    @RequestMapping("/self")
    public BaseResponse<List<InterfaceInfoVO>> analyzeSelfInterfaceInfo(HttpServletRequest request){
        User user = userService.getLoginUser(request);
        if(user==null){
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        return userinterfaceinfoService.analyzeSelfInterfaceInfo(user.getId());
    }


}
