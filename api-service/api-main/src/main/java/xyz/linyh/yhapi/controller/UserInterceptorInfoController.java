package xyz.linyh.yhapi.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.DeleteRequest;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.CommonConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.vo.InterfaceInfoVO;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.model.userinterfaceinfo.entitys.UserInterfaceinfo;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoQueryRequest;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoUpdateRequest;
import xyz.linyh.model.userinterfaceinfo.dto.UserInterfaceInfoAddRequest;
import xyz.linyh.yhapi.service.UserService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 对接口的增删改查
 *
 *
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterceptorInfoController {


    @Autowired
    private UserinterfaceinfoService userInterfaceinfoService;

    @Autowired
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserInterfaceinfo userInterfaceInfo = new UserInterfaceinfo();
        userInterfaceInfo.setStatus(0);

        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        // 校验参数是否正确
        userInterfaceinfoService.validInterfaceInfo(userInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceinfoService.save(userInterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        long newInterfaceInfoId = userInterfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);

    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceinfo oldInterfaceInfo = userInterfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceinfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param userInterfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceinfo userInterfaceInfo = new UserInterfaceinfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceinfoService.validInterfaceInfo(userInterfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = userInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceinfo oldInterfaceInfo = userInterfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceinfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }



    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserInterfaceinfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceinfo userInterfaceInfo = userInterfaceinfoService.getById(id);
        return ResultUtils.success(userInterfaceInfo);
    }




    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<Page<UserInterfaceinfo>> listInterfaceInfo(InterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        UserInterfaceinfo userInterfaceInfoQuery = new UserInterfaceinfo();
        if (userInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        }
        QueryWrapper<UserInterfaceinfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        Page<UserInterfaceinfo> page = new Page<>(userInterfaceInfoQueryRequest.getCurrent(),userInterfaceInfoQueryRequest.getPageSize());
        Page<UserInterfaceinfo> userInterfaceInfoList = userInterfaceinfoService.page(page, queryWrapper);
//        List<UserInterfaceinfo> userInterfaceInfoList = userInterfaceinfoService.list(queryWrapper);
        return ResultUtils.success(userInterfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserInterfaceinfo>> listPnterfaceInfoByPage(InterfaceInfoQueryRequest userInterfaceInfoQueryRequest, HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceinfo userInterfaceInfoQuery = new UserInterfaceinfo();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
//        String content = userInterfaceInfoQuery.getContent();
        // content 需支持模糊搜索
//        userInterfaceInfoQuery.setContent(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceinfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
//        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceinfo> userInterfaceInfoPage = userInterfaceinfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userInterfaceInfoPage);
    }

    // endregion

    /**
     * 获取一个接口的详细信息，包括调用次数
     * @return
     */
    @GetMapping("/detailwithtotal")
    public BaseResponse<InterfaceInfoVO> getInterfaceAllDataByInterfaceId(HttpServletRequest request, Long interfaceId){
        if(interfaceId==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"接口id不能为空");
        }

       return userInterfaceinfoService.getInterfaceAllDataByInterfaceId(interfaceId);
    }

    @GetMapping("detailwithremnum")
    public BaseResponse<InterfaceInfoVO> getInterfaceByInterfaceIdAndUserId(Long interfaceId,HttpServletRequest request){
        if(interfaceId==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"接口id不能为空");
        }

        User user = userService.getLoginUser(request);
        if(user==null){
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        InterfaceInfoVO userInterfaceInfo = userInterfaceinfoService.getInterfaceWithRemNumByInterfaceId(user.getId(), interfaceId);
        return ResultUtils.success(userInterfaceInfo);
    }



    @GetMapping("/experience")
    public BaseResponse<UserInterfaceinfo> getExperienceCount(HttpServletRequest request, Long id){
        if(id==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"接口id不能为空");
        }

//        获取用户
        User user = userService.getLoginUser(request);
        if(user==null){
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
//        判断是否是首次体验
        UserInterfaceinfo userInterfaceinfo = userInterfaceinfoService.getOne(Wrappers.<UserInterfaceinfo>lambdaQuery().
                eq(UserInterfaceinfo::getUserId, user.getId())
                .eq(UserInterfaceinfo::getInterfaceId, id));
        if(userInterfaceinfo!=null){
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR,"无法重复获取次数");
        }
//        增加调用次数
        Boolean isSave = userInterfaceinfoService.addCountIfNo(id, user.getId(), 10);
        userInterfaceinfo = userInterfaceinfoService.getOne(Wrappers.<UserInterfaceinfo>lambdaQuery().eq(UserInterfaceinfo::getUserId, user.getId()).eq(UserInterfaceinfo::getInterfaceId, id));
        return ResultUtils.success(userInterfaceinfo);
    }

}
