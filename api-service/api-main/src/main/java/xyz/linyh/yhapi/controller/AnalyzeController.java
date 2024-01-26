package xyz.linyh.yhapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoAnalyzeDto;
import xyz.linyh.model.interfaceinfo.vo.InterfaceInfoVO;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.service.UserService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lin
 */
@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    @Autowired
    private UserinterfaceinfoService userinterfaceinfoService;

    @Autowired
    private UserService userService;

    /**
     * 获取用户调用次数前几的接口数据
     *
     * @return
     */
    @GetMapping("/all")
    public BaseResponse analyzeAllInterfaceInfo(InterfaceInfoAnalyzeDto dto) {
        if(dto==null || dto.getTotal()==null || dto.getCurrent() == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"需要输入一次展示多少个数据");
        }

        List<InterfaceInfoVO> interfaceInfoVOS = userinterfaceinfoService.analyzeAllInterfaceInfo(dto.getCurrent(),dto.getTotal(),null);

        return ResultUtils.success(interfaceInfoVOS);
    }


    /**
     * 分析自己发布的调用次数前5的接口
     *
     * @param request
     * @return
     */
    @GetMapping("/self")
    public BaseResponse<List<InterfaceInfoVO>> analyzeSelfInterfaceInfo(InterfaceInfoAnalyzeDto dto,HttpServletRequest request) {
        if(dto==null || dto.getTotal()==null || dto.getCurrent() == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"需要输入一次展示多少个数据");
        }

        User user = userService.getLoginUser(request);

        List<InterfaceInfoVO> interfaceInfoVOS = userinterfaceinfoService.analyzeSelfInterfaceInfo(dto.getCurrent(),dto.getTotal(), user.getId(),null);
        return ResultUtils.success(interfaceInfoVOS);
    }


}
