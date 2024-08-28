package xyz.linyh.yhapi.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.DeleteRequest;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.ducommon.constant.RedisConstant;
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
import javax.servlet.http.HttpServletResponse;

/**
 * 对平台接口数据修改接口
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
@RefreshScope
public class InterceptorInfoController {

    @Autowired
    private InterfaceinfoService interfaceinfoService;

    @Autowired
    private UserinterfaceinfoService userinterfaceinfoService;

    @Autowired
    private UserService userService;


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
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
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
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR);
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
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
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
    @CacheEvict(cacheNames = RedisConstant.INTERFACE_PAGE_CACHE_NAMES, allEntries = true)
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
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
     * 修改接口状态（管理员和接口拥有者可用）
     *
     * @param dto
     * @param request
     * @return
     */
    @PutMapping("/updateStatus")
    public BaseResponse<Boolean> updateStatus(@RequestBody UpdateStatusDto dto, HttpServletRequest request) {
//        判断参数是否正确
        if (dto == null || dto.getInterfaceId() <= 0 || dto.getStatus() == null || (dto.getStatus().equals(InterfaceInfoConstant.STATIC_USE) && dto.getStatus().equals(InterfaceInfoConstant.STATIC_NOT_USE))) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        User user = userService.getLoginUser(request);
        boolean result = interfaceinfoService.updateInterfaceInfoStatus(dto, user);

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
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }
        Interfaceinfo interfaceInfo = interfaceinfoService.getById(id);
//        脱敏
        interfaceInfo.setHost("");
        return ResultUtils.success(interfaceInfo);
    }


    /**
     * 执行对应id接口
     *
     * @param interfaceInfoInvokeRequest 接口数据
     * @param request                    request
     */
//    todo 只是简单的，需要改为根据每一个请求获取请求参数，然后传递
    @PostMapping("/invoke")
    public String invokeInterfaceById(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                      HttpServletRequest request, HttpServletResponse response) {
//        判断参数是否有效
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "参数错误");
        }

//        判断接口是否有效
        Interfaceinfo interfaceInfo = interfaceinfoService.getOne(Wrappers.<Interfaceinfo>lambdaQuery()
                .eq(Interfaceinfo::getId, interfaceInfoInvokeRequest.getId())
                .eq(Interfaceinfo::getStatus, InterfaceInfoConstant.STATIC_USE));
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "不存在这个接口或接口已经下线");
        }

        User user = userService.getLoginUser(request);

//        判断是否有调用次数或积分是否够
        InterfaceInfoInvokeParams interfaceInfoInvokeParams = new InterfaceInfoInvokeParams();
        InterfaceInfoInvokePayType payType = userinterfaceinfoService.isInvokeAndGetPayType(interfaceInfoInvokeRequest.getId(), user.getId(), interfaceInfo.getPointsRequired());
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
    public BaseResponse<Page<Interfaceinfo>> listInterfaceInfo(InterfaceInfoQueryBaseDto interfaceInfoQueryRequest) {
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
    public BaseResponse<Page<Interfaceinfo>> listInterfaceInfoByPage(InterfaceInfoQueryBaseDto interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }
        Page<Interfaceinfo> interfaceInfoPage = interfaceinfoService.selectInterfaceInfoByPage(interfaceInfoQueryRequest);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 用户获取分页自己的所有接口
     * 条件查询
     */
    @GetMapping("/self")
    public BaseResponse getSelfInterfaceInfo(InterfaceInfoQueryBaseDto interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null || interfaceInfoQueryRequest.getPageSize() == 0L || interfaceInfoQueryRequest.getCurrent() == 0L) {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "查询参数不能为空");
        }

        User user = userService.getLoginUser(request);
        interfaceInfoQueryRequest.setUserId(user.getId());
        Page<Interfaceinfo> interfaceinfoPage = interfaceinfoService.selectInterfaceInfoByPage(interfaceInfoQueryRequest);

        return ResultUtils.success(interfaceinfoPage);
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
