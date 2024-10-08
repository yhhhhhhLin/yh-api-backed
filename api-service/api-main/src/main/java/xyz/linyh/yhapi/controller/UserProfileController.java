package xyz.linyh.yhapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.UserProfileConstant;
import xyz.linyh.model.interfaceinfo.vo.InterfaceInfoVO;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.model.user.vo.UserProfileVo;
import xyz.linyh.yhapi.service.UserService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户信息
 */
@RestController
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserinterfaceinfoService userinterfaceinfoService;

    /**
     * 获取某一个用户的信息（包括发布的接口数量，接口调用总次数等等数据)
     * @param account
     * @param request
     * @return
     */
    @GetMapping("/user/profile/{account}")
    public BaseResponse getUserProfile(@PathVariable String account, HttpServletRequest request) {
        if (account == null) {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "参数错误");
        }

        User user = userService.getLoginUser(request);
        UserProfileVo userProfileVo = userService.getUserProfile(user, account);

        return ResultUtils.success(userProfileVo);
    }

    /**
     * 获取某一个业务列表
     *
     * @param type 业务类型
     * @return 返回BaseResponse对象
     */
    @GetMapping("/user/business-list")
    public BaseResponse getBusinessList(String type, Long userId,Integer page, Integer size) {
        if (type == null) {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "参数错误");
        }

        switch (type) {
            case UserProfileConstant.BUSINESS_TYPE_INTERFACE:
                List<InterfaceInfoVO> InterfaceInfoVOs = userinterfaceinfoService.analyzeSelfInterfaceInfo(page,size,userId,null);
                return ResultUtils.success(InterfaceInfoVOs);
        }


        return null;
    }
}
