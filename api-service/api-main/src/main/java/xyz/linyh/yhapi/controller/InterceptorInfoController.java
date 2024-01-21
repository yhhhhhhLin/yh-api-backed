package xyz.linyh.yhapi.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.DeleteRequest;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.InterfaceInfoInvokeParams;
import xyz.linyh.model.interfaceinfo.InterfaceInfoInvokePayType;
import xyz.linyh.model.interfaceinfo.dto.*;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.service.InterfaceinfoService;
import xyz.linyh.yhapi.service.UserService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import javax.servlet.http.HttpServletRequest;

/**
 * 对接口的增删改查
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterceptorInfoController {

    @Autowired
    private InterfaceinfoService interfaceinfoService;

    @Autowired
    private UserinterfaceinfoService userinterfaceinfoService;

    @Autowired
    private UserService userService;


    // region 增删改查

    /**
     * 管理员创建对应数据接口
     * （不需要审核）
     *
     * @param interfaceInfoAddRequest 接口数据
     * @param request                 request
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceInfo = new Interfaceinfo();
        interfaceInfo.setStatus(1);
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验参数是否正确
        interfaceinfoService.validInterfaceInfoParams(interfaceInfo, true);

        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());

        boolean result = interfaceinfoService.addInterfaceInfo(interfaceInfo);

        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

//        刷新网关的缓存接口数据
        interfaceinfoService.updateGatewayCache();

        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 更新接口数据（管理员和接口拥有者可用）
     *
     * @param interfaceInfoUpdateRequest 接口数据
     * @param request                    request
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Interfaceinfo interfaceInfo = new Interfaceinfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);

        // 参数校验
        interfaceinfoService.validInterfaceInfoParams(interfaceInfo, false);

        User user = userService.getLoginUser(request);

        boolean result = interfaceinfoService.updateInterfaceInfo(user, interfaceInfo);

        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(result);
    }


    /**
     * 删除对应id接口（管理员和接口拥有者可用）
     *
     * @param deleteRequest 接口数据
     * @param request       request
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        validInterface(id, user, request);

        boolean result = interfaceinfoService.removeById(id);
//        todo
        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(result);
    }

    /**
     * 接口上线（管理员和接口拥有者可用）
     *
     * @param idRequest 接口id
     * @param request   request
     */
    @PostMapping("/online")
    public BaseResponse onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                            HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = idRequest.getId();

        validInterface(id, user, request);

        LambdaUpdateWrapper<Interfaceinfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Interfaceinfo::getId, id)
                .set(Interfaceinfo::getStatus, 1);
        boolean result = interfaceinfoService.update(wrapper);

        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(result);
    }

    /**
     * 接口下线（管理员和接口拥有者可用）
     *
     * @param idRequest 接口id
     * @param request   request
     */
    @PostMapping("/offline")
    public BaseResponse offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                             HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = idRequest.getId();

        validInterface(id, user, request);

        LambdaUpdateWrapper<Interfaceinfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Interfaceinfo::getId, id)
                .set(Interfaceinfo::getStatus, 0);
        boolean result = interfaceinfoService.update(wrapper);

//        todo
        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取接口详细数据
     *
     * @param id 接口id
     * @return 接口详细数据
     */
    @GetMapping("/get")
    public BaseResponse<Interfaceinfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceInfo = interfaceinfoService.getById(id);
//        脱敏
        interfaceInfo.setHost("");
        return ResultUtils.success(interfaceInfo);
    }


//    先全部用管理员ak和sk发送，后面改为根据每一个用户发送

    /**
     * 执行对应id接口
     *
     * @param interfaceInfoInvokeRequest 接口数据
     * @param request                    request
     */
//    todo 只是简单的，需要改为根据每一个请求获取请求参数，然后传递
    @PostMapping("/invoke")
    public String invokeInterfaceById(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                      HttpServletRequest request) {
//        判断参数是否有效
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//        判断接口是否有效
        Interfaceinfo interfaceInfo = interfaceinfoService.getById(interfaceInfoInvokeRequest.getId());
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "不存在这个接口");
        }

        User user = userService.getLoginUser(request);

//        判断是否有调用次数或积分是否够
        InterfaceInfoInvokeParams interfaceInfoInvokeParams = new InterfaceInfoInvokeParams();
        InterfaceInfoInvokePayType payType = userinterfaceinfoService.isInvokeAndGetPayType(interfaceInfoInvokeRequest.getId(), user.getId(),interfaceInfo.getPointsRequired());
        interfaceInfoInvokeParams.setPayType(payType);

//        添加请求参数 并发送请求到网关
        BeanUtils.copyProperties(interfaceInfoInvokeRequest, interfaceInfoInvokeParams);
        return interfaceinfoService.invokeInterface(user, interfaceInfo, interfaceInfoInvokeRequest);

    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest 查询条件
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<Page<Interfaceinfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        Interfaceinfo interfaceInfoQuery = new Interfaceinfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<Interfaceinfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        Page<Interfaceinfo> page = new Page<>(interfaceInfoQueryRequest.getCurrent(), interfaceInfoQueryRequest.getPageSize());
        Page<Interfaceinfo> interfaceInfoList = interfaceinfoService.page(page, queryWrapper);
//        List<Interfaceinfo> interfaceInfoList = interfaceinfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取所有接口列表
     *
     * @param interfaceInfoQueryRequest 查询条件
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Interfaceinfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Interfaceinfo> interfaceInfoPage = interfaceinfoService.selectInterfaceInfoByPage(interfaceInfoQueryRequest);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 用户获取分页自己的所有接口
     * 条件查询名称 方法 uri 状态
     */
    @PostMapping("/self")
    public BaseResponse getSelfInterfaceInfo(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null || interfaceInfoQueryRequest.getPageSize() == 0L || interfaceInfoQueryRequest.getCurrent() == 0L) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "查询参数不能为空");
        }

        User user = userService.getLoginUser(request);
        if (user == null) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "用户不能没有登录");
        }

        LambdaQueryWrapper<Interfaceinfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interfaceinfo::getUserId, user.getId())
                .eq(!StrUtil.isBlank(interfaceInfoQueryRequest.getMethod()), Interfaceinfo::getMethod, interfaceInfoQueryRequest.getMethod())
                .eq(interfaceInfoQueryRequest.getStatus() != null, Interfaceinfo::getStatus, interfaceInfoQueryRequest.getStatus())
                .like(!StrUtil.isBlank(interfaceInfoQueryRequest.getName()), Interfaceinfo::getName, interfaceInfoQueryRequest.getName())
                .like(!StrUtil.isBlank(interfaceInfoQueryRequest.getUri()), Interfaceinfo::getUri, interfaceInfoQueryRequest.getUri());
        Page<Interfaceinfo> page = new Page<>(interfaceInfoQueryRequest.getCurrent(), interfaceInfoQueryRequest.getPageSize());
        Page<Interfaceinfo> interfaceInfoPage = interfaceinfoService.page(page, wrapper);

        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 普通用户创建对应数据接口
     * （要审核）
     *
     * @param interfaceInfoAddRequest 接口信息
     * @param request                 request
     */
    @PostMapping("/useradd")
    public BaseResponse<Long> addInterfaceInfoByUser(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceInfo = new Interfaceinfo();
        interfaceInfo.setStatus(0);
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验参数是否正确
        interfaceinfoService.validInterfaceInfoParams(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
//        保存到待审审核的地方 todo
//        boolean result = interfaceinfoService.save(interfaceInfo);
        if (!true) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
//        todo
//        interfaceinfoService.updateGatewayCache();

        return ResultUtils.success(newInterfaceInfoId);
    }


    /**
     * 对保存到数据库的接口进行校验
     *
     * @param id   接口id
     * @param user 用户
     */
    private void validInterface(long id, User user, HttpServletRequest request) {
    }

}
