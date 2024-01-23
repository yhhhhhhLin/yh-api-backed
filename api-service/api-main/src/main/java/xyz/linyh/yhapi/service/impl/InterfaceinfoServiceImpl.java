package xyz.linyh.yhapi.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.CommonConstant;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.dto.GRequestParamsDto;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoInvokeRequest;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoQueryRequest;
import xyz.linyh.model.interfaceinfo.dto.UpdateStatusDto;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yapiclientsdk.client.ApiClient;
import xyz.linyh.yapiclientsdk.entitys.InterfaceParams;
import xyz.linyh.yhapi.mapper.InterfaceinfoMapper;
import xyz.linyh.yhapi.service.InterfaceinfoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lin
 * @description 针对表【interfaceinfo(接口信息表)】的数据库操作Service实现
 * @createDate 2023-09-03 19:31:19
 */
@Service
@Slf4j
public class InterfaceinfoServiceImpl extends ServiceImpl<InterfaceinfoMapper, Interfaceinfo>
        implements InterfaceinfoService {


    /**
     * 对接口信息进行校验
     *
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfoParams(Interfaceinfo interfaceInfo, boolean add) {

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = interfaceInfo.getName();
        String method = interfaceInfo.getMethod();
        String description = interfaceInfo.getDescription();
        String uri = interfaceInfo.getUri();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        Integer pointsRequired = interfaceInfo.getPointsRequired();

        if (pointsRequired == null || pointsRequired < 0) {
            pointsRequired = 0;
        }

//        在uri里面不能包含空格
        if (interfaceInfo.getUri() != null && interfaceInfo.getUri().contains(" ")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址不能包含空格");
        }

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, method, description, uri, requestHeader, responseHeader)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 255) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }

    }


    @Override
    public Interfaceinfo getInterfaceInfoByURI(String interfaceURI, String method) {
        if (interfaceURI == null || method == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interfaceinfo interfaceinfo = this.getOne(Wrappers.<Interfaceinfo>lambdaQuery()
                .eq(Interfaceinfo::getUri, interfaceURI)
                .eq(Interfaceinfo::getMethod, method)
        );
        return interfaceinfo;
    }

    /**
     * 刷新网关的接口缓存数据
     *
     * @return
     */
    @Autowired
    private ApiClient apiClient;

    @Override
    public Boolean updateGatewayCache() {

        String baseUrl = apiClient.getBaseUrl();
        if (baseUrl == null) {
            return false;
        }
//        将最后的地址后面的/interface去掉
        if (baseUrl.endsWith("/interface")) {
            baseUrl = baseUrl.replace("/interface", "");
        }
//      TODO  后面改为常量
        log.info("开始刷新gateway缓存,地址为{}....", baseUrl + "/yhapi/routes");

        try {
            HttpResponse execute = HttpRequest.get(baseUrl + "/yhapi/routes").execute();
        } catch (Exception e) {
            log.info("刷新缓存失败");
        }

        return true;
    }

    /**
     * 判断某一个接口是否上线
     *
     * @param interfaceId
     * @return
     */
    @Override
    public Boolean isOnline(Long interfaceId) {
        Interfaceinfo interfaceInfo = this.getById(interfaceId);
        if (interfaceInfo == null || interfaceInfo.getStatus().equals(Integer.valueOf(InterfaceInfoConstant.STATIC_NOT_USE))) {
            return false;
        }
        return true;
    }

    /**
     * 执行某一个接口(判断是否有携带参数，然后发送给sdk处理请求)
     *
     * @param interfaceInfo
     * @param interfaceInfoInvokeRequest
     * @return
     */
    @Override
    public String invokeInterface(User user, Interfaceinfo interfaceInfo, InterfaceInfoInvokeRequest interfaceInfoInvokeRequest) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口执行者id不能为空");
        }
        apiClient.setAccessKey(user.getAccessKey());
        apiClient.setSecretKey(user.getSecretKey());

        String response = null;
//        请求参数
        List<GRequestParamsDto> getRequestParams = interfaceInfoInvokeRequest.getGetRequestParams();
//        请求体参数
        String requestParams = interfaceInfoInvokeRequest.getRequestParams();

//        如果没有请求参数，那么直接用简单的方式发送请求
        if (!haveParamsOrBody(getRequestParams, requestParams)) {

            try {
                response = apiClient.request(interfaceInfo.getUri(), interfaceInfo.getMethod());
            } catch (Exception e) {
                log.error("发送请求失败");
                log.error(e.getMessage());
            }
        } else {

            InterfaceParams interfaceParams = new InterfaceParams();
            if (getRequestParams != null && getRequestParams.size() > 0) {
                HashMap<String, Object> params = new HashMap<>();
                for (GRequestParamsDto dto : getRequestParams) {
                    params.put(dto.getRequestParmK(), dto.getRequestParmV());
                }
                interfaceParams.setRequestParams(params);
            }

            if (requestParams != null) {
                Gson gson = new Gson();
                Map<String, Object> bodyParams = gson.fromJson(interfaceInfoInvokeRequest.getRequestParams(), Map.class);
                interfaceParams.setRequestBody(bodyParams);
            }
            interfaceParams.setRequestMethod(interfaceInfo.getMethod());
//            发送请求 添加请求头 todo
            try {
                log.info("请求接口参数为:{}", interfaceParams);
                response = apiClient.request(interfaceInfo.getUri(), interfaceParams);

            } catch (Exception e) {
                log.error("发送请求失败");
                log.error(e.getMessage());
            }
        }

//        对响应进行处理
        log.info("{} 接口的相应数据为{}", interfaceInfo.getUri(), response);

        return response;
    }

    @Override
    public boolean addInterfaceInfo(Interfaceinfo interfaceInfo) {
//        判断接口uri不能重复
        List<Interfaceinfo> dbInterfaceInfos = this.list(Wrappers.<Interfaceinfo>lambdaQuery().eq(Interfaceinfo::getUri, interfaceInfo.getUri()));
        if (dbInterfaceInfos != null && dbInterfaceInfos.size() > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址不能重复");
        }
        return this.save(interfaceInfo);
    }

    @Override
    public Page<Interfaceinfo> selectInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();

        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<Interfaceinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        queryWrapper.like(StringUtils.isNotBlank(interfaceInfoQueryRequest.getName()), "name", interfaceInfoQueryRequest.getName());
        queryWrapper.like(StringUtils.isNotBlank(interfaceInfoQueryRequest.getDescription()), "description", interfaceInfoQueryRequest.getDescription());
        queryWrapper.eq(interfaceInfoQueryRequest.getUserId() != null, "userId", interfaceInfoQueryRequest.getUserId());
        queryWrapper.eq(StringUtils.isNotBlank(interfaceInfoQueryRequest.getMethod()), "method", interfaceInfoQueryRequest.getMethod());
        queryWrapper.eq(StringUtils.isNotBlank(interfaceInfoQueryRequest.getUri()), "uri", interfaceInfoQueryRequest.getUri());
        queryWrapper.eq(StringUtils.isNotBlank(interfaceInfoQueryRequest.getHost()), "host", interfaceInfoQueryRequest.getHost());
        queryWrapper.eq(interfaceInfoQueryRequest.getStatus() != null, "status", interfaceInfoQueryRequest.getStatus());

        return this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public boolean updateInterfaceInfo(User user, Interfaceinfo interfaceInfo) {
        if (interfaceInfo == null || interfaceInfo.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口数据或id不能为空");
        }
        validInterfaceInfo(interfaceInfo.getId(), user);
//        判断数据库中是否有重复的uri
        Interfaceinfo dbInterfaceInfo = this.getOne(
                Wrappers.<Interfaceinfo>lambdaQuery()
                        .eq(Interfaceinfo::getUri, interfaceInfo.getUri())
                        .ne(Interfaceinfo::getId, interfaceInfo.getId()));
        if (dbInterfaceInfo != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址不能重复");
        }
        return this.updateById(interfaceInfo);
    }

    /**
     * 判断接口是否存在获取用户是否有权限修改这个接口
     *
     * @param interfaceInfoId
     * @param user
     */
    @Override
    public void validInterfaceInfo(Long interfaceInfoId, User user) {
        if (interfaceInfoId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id不能为空");
        }
//        判断接口是否存在
        Interfaceinfo oldInterfaceInfo = this.getById(interfaceInfoId);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }

        //判断用户是否有权限修改这个接口 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !"admin".equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    @Override
    public boolean updateInterfaceInfoStatus(UpdateStatusDto dto, User user) {

//        判断这个接口只能是管理员或接口拥有着可以修改
        this.validInterfaceInfo(dto.getInterfaceId(), user);

        boolean result = this.update(Wrappers.<Interfaceinfo>lambdaUpdate()
                .eq(Interfaceinfo::getId, dto.getInterfaceId())
                .set(Interfaceinfo::getStatus, dto.getStatus()));
        this.updateGatewayCache();
        return result;
    }


    private Boolean haveParamsOrBody(List<GRequestParamsDto> getRequestParams, String requestParams) {

//        如果全是为空的，那么就直接返回false。如果返回true，那么就是有其中一个

        return (requestParams != null || (getRequestParams != null && !getRequestParams.isEmpty()));
    }

}




