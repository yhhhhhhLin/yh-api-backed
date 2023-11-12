package xyz.linyh.yhapi.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.DeleteRequest;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.CommonConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.ducommon.requestParms.InterfaceParams;
import xyz.linyh.model.interfaceinfo.dto.*;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yapiclientsdk.client.ApiClient;
import xyz.linyh.yhapi.annotation.AuthCheck;
import xyz.linyh.yhapi.service.InterfaceinfoService;
import xyz.linyh.yhapi.service.UserService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对接口的增删改查
 *
 *
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterceptorInfoController {

    @Resource
    private InterfaceinfoService interfaceinfoService;

    @Resource
    private UserinterfaceinfoService userinterfaceinfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 管理员创建对应数据接口
     * （不需要审核）
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
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
        interfaceinfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceinfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        Boolean aBoolean = interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 更新接口数据（管理员和接口拥有者可用）
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
//    @AuthCheck(mustRole = "admin")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceInfo = new Interfaceinfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceinfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        Interfaceinfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceinfoService.updateById(interfaceInfo);
//        刷新网关接口数据
        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(result);
    }

    /**
     * 删除对应id接口（管理员和接口拥有者可用）
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
        Interfaceinfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceinfoService.removeById(id);

        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(b);
    }

    /**
     * 接口上线（管理员和接口拥有者可用）
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    public BaseResponse onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                          HttpServletRequest request){
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = idRequest.getId();
        // 判断是否存在
        Interfaceinfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        LambdaUpdateWrapper<Interfaceinfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Interfaceinfo::getId,id)
                .set(Interfaceinfo::getStatus,1);
        boolean result = interfaceinfoService.update(wrapper);

        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(result);
    }

    /**
     * 接口下线（管理员和接口拥有者可用）
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    public BaseResponse offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                          HttpServletRequest request){
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = idRequest.getId();
        // 判断是否存在
        Interfaceinfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        LambdaUpdateWrapper<Interfaceinfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Interfaceinfo::getId,id)
                .set(Interfaceinfo::getStatus,0);
        boolean result = interfaceinfoService.update(wrapper);

        interfaceinfoService.updateGatewayCache();
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取接口详细数据
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Interfaceinfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceInfo = interfaceinfoService.getById(id);
//        过敏
        interfaceInfo.setHost("");
        return ResultUtils.success(interfaceInfo);
    }

//    先全部用管理员ak和sk发送，后面改为根据每一个用户发送
    /**
     * 执行对应id接口
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
//    todo 只是简单的，需要改为根据每一个请求获取请求参数，然后传递
    @PostMapping("/invoke")
    public String invokeInterfaceById(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                      HttpServletRequest request){
//        判断参数是否有效
        if(interfaceInfoInvokeRequest==null ||interfaceInfoInvokeRequest.getId()==null ||interfaceInfoInvokeRequest.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//        判断接口是否有效
        Interfaceinfo interfaceInfo = interfaceinfoService.getById(interfaceInfoInvokeRequest.getId());
        if(interfaceInfo==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"不存在这个接口");
        }

        User user = userService.getLoginUser(request);

//        判断是否还有调用次数
        Boolean isInvoke = userinterfaceinfoService.isInvoke(interfaceInfoInvokeRequest.getId(), user.getId());
        if(!isInvoke){
            throw new BusinessException(ErrorCode.NOT_INVOKE_NUM_ERROR,"没有调用次数");
        }

//        获取用户的ak和sk
        String accessKey = user.getAccessKey();
        String secretKey = user.getSecretKey();


//        添加请求参数 并发送请求到网关
        ApiClient apiClient = new ApiClient(accessKey, secretKey);
        String response =null;

//        请求参数
        List<GRequestParamsDto> getRequestParams = interfaceInfoInvokeRequest.getGetRequestParams();
//        请求体参数
        String requestParams = interfaceInfoInvokeRequest.getRequestParams();
//        如果没有请求参数，那么直接用简单的方式发送请求
        if((getRequestParams==null && requestParams==null) ||(requestParams==null && getRequestParams.size()==0)){

           response = apiClient.request(interfaceInfo.getUri(), interfaceInfo.getMethod());
        }else{

            InterfaceParams interfaceParams = new InterfaceParams();
            if(getRequestParams!=null && getRequestParams.size()>0){
                HashMap<String, Object> params = new HashMap<>();
                for(GRequestParamsDto dto:getRequestParams){
                    params.put(dto.getRequestParmK(),dto.getRequestParmV());
                }
                interfaceParams.setRequestParams(params);
            }

            if(requestParams!=null){
                Gson gson = new Gson();
                Map<String, Object> bodyParams = gson.fromJson(interfaceInfoInvokeRequest.getRequestParams(), Map.class);
                interfaceParams.setRequestBody(bodyParams);
            }
            interfaceParams.setRequestMethod(interfaceInfo.getMethod());
//            发送请求 添加请求头 todo
            response = apiClient.request(interfaceInfo.getUri(),interfaceParams);
        }

//        对响应进行处理
        System.out.println(response);

        return response;
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<Page<Interfaceinfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        Interfaceinfo interfaceInfoQuery = new Interfaceinfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<Interfaceinfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        Page<Interfaceinfo> page = new Page<>(interfaceInfoQueryRequest.getCurrent(),interfaceInfoQueryRequest.getPageSize());
        Page<Interfaceinfo> interfaceInfoList = interfaceinfoService.page(page, queryWrapper);
//        List<Interfaceinfo> interfaceInfoList = interfaceinfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取所有接口列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Interfaceinfo>> listPnterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceInfoQuery = new Interfaceinfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
//        String content = interfaceInfoQuery.getContent();
        // content 需支持模糊搜索
//        interfaceInfoQuery.setContent(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Interfaceinfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
//        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Interfaceinfo> interfaceInfoPage = interfaceinfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 用户获取分页自己的所有接口
     * 条件查询名称 方法 uri 状态
     * @return
     */
    @PostMapping("/self")
    public BaseResponse getSelfInterfaceInfo(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,HttpServletRequest request){
        if(interfaceInfoQueryRequest==null || interfaceInfoQueryRequest.getPageSize()==0L || interfaceInfoQueryRequest.getCurrent()==0L){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"查询参数不能为空");
        }

        User user = userService.getLoginUser(request);
        if(user==null){
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR,"用户不能没有登录");
        }

        LambdaQueryWrapper<Interfaceinfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interfaceinfo::getUserId,user.getId())
                .eq(!StrUtil.isBlank(interfaceInfoQueryRequest.getMethod()),Interfaceinfo::getMethod,interfaceInfoQueryRequest.getMethod())
                .eq(interfaceInfoQueryRequest.getStatus()!=null,Interfaceinfo::getStatus,interfaceInfoQueryRequest.getStatus())
                .like(!StrUtil.isBlank(interfaceInfoQueryRequest.getName()),Interfaceinfo::getName,interfaceInfoQueryRequest.getName())
                .like(!StrUtil.isBlank(interfaceInfoQueryRequest.getUri()),Interfaceinfo::getUri,interfaceInfoQueryRequest.getUri());
        Page<Interfaceinfo> page = new Page<>(interfaceInfoQueryRequest.getCurrent(),interfaceInfoQueryRequest.getPageSize());
        Page<Interfaceinfo> interfaceInfoPage = interfaceinfoService.page(page, wrapper);

        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 普通用户创建对应数据接口
     * （要审核）
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
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
        interfaceinfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
//        保存到待审审核的地方 todo
//        boolean result = interfaceinfoService.save(interfaceInfo);
        if (!true) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();

        interfaceinfoService.updateGatewayCache();

        return ResultUtils.success(newInterfaceInfoId);
    }


    // endregion

}
