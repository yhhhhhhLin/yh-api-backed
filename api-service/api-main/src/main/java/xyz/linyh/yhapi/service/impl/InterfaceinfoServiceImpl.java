package xyz.linyh.yhapi.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.common.InterfaceTypeEnum;
import xyz.linyh.ducommon.constant.CommonConstant;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.ducommon.constant.RedisConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.ducommon.utils.DatasourceApiTokenUtils;
import xyz.linyh.ducommon.utils.JwtUtils;
import xyz.linyh.ducommon.utils.TimeUtils;
import xyz.linyh.model.apitoken.entitys.ApiTokenRel;
import xyz.linyh.model.base.dtos.CheckNameDto;
import xyz.linyh.model.datasource.dtos.AddDataSourceApiDto;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;
import xyz.linyh.model.interfaceinfo.dto.*;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yapiclientsdk.client.ApiClient;
import xyz.linyh.yapiclientsdk.entitys.InterfaceParams;
import xyz.linyh.yhapi.datasource.DataSourceClient;
import xyz.linyh.yhapi.factory.DataSourceClientFactory;
import xyz.linyh.yhapi.factory.GenSqlFactory;
import xyz.linyh.yhapi.helper.GenSql;
import xyz.linyh.yhapi.mapper.InterfaceinfoMapper;
import xyz.linyh.yhapi.service.*;

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

    @Autowired
    private InterfaceinfoMapper interfaceinfoMapper;

    @Autowired
    private InterfaceInfoDispatchInfoService interfaceInfoDispatchInfoService;

    @Autowired
    private DscInterfaceColumnService dscInterfaceColumnService;

    @Autowired
    private DscInfoService dscInfoService;

    @Autowired
    private ApiTokenRelService apiTokenRelService;

    /**
     * 对接口信息进行校验
     *
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfoParams(Interfaceinfo interfaceInfo, boolean add) {

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
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
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "接口地址不能包含空格");
        }

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, method, description, uri, requestHeader, responseHeader)) {
                throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 255) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "内容过长");
        }

    }


    @Override
    public Interfaceinfo getInterfaceInfoByURI(String interfaceURI, String method) {
        if (interfaceURI == null || method == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
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
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "接口执行者id不能为空");
        }
//        clint增加数据源类型参数
        apiClient.setAccessKey(user.getAccessKey());
        apiClient.setSecretKey(user.getSecretKey());
        apiClient.setInterfaceType(interfaceInfoInvokeRequest.getInterfaceType());

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

                log.error("发送请求失败,response为:{},{}", e.getMessage(), e);
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
            interfaceParams.setInterfaceType(interfaceInfo.getInterfaceType());
//            发送请求 添加请求头
            try {
                log.info("请求接口参数为:{}", interfaceParams);
                response = apiClient.request(interfaceInfo.getUri(), interfaceParams);

            } catch (Exception e) {
                log.error("发送请求失败,{}", e.getMessage(), e);
            }
        }

//        对响应进行处理
        log.info("{} 接口的相应数据为{}", interfaceInfo.getUri(), response);

        return response;
    }

    @Override
    @CacheEvict(cacheNames = RedisConstant.INTERFACE_PAGE_CACHE_NAMES, allEntries = true)
    @Transactional
    public Long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest) {
        Interfaceinfo interfaceInfo = new Interfaceinfo();
        interfaceInfo.setStatus(1);
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验参数是否正确
        this.validInterfaceInfoParams(interfaceInfo, true);

//        判断接口uri不能重复 TODO 增加token后优化成单个用户之间不能重复就可以
        List<Interfaceinfo> dbInterfaceInfos = this.list(Wrappers.<Interfaceinfo>lambdaQuery().eq(Interfaceinfo::getUri, interfaceInfo.getUri()));
        if (dbInterfaceInfos != null && !dbInterfaceInfos.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "接口地址不能重复");
        }

        this.save(interfaceInfo);

//        如果是数据源api
        Integer interfaceType = interfaceInfoAddRequest.getInterfaceType();
        if (interfaceType.equals(InterfaceTypeEnum.DATABASE_INTERFACE.getCode())) {
            AddDataSourceApiDto dataSourceApiParams = interfaceInfoAddRequest.getDataSourceApiParams();
//            保存查询列信息
            dscInterfaceColumnService.saveBatch(dataSourceApiParams.getSearchColumns());

//          创建调度信息
            interfaceInfoDispatchInfoService.createDispatchInfo(interfaceInfo.getId(), dataSourceApiParams.getDispatchInfo());

//            数据源接口输入和输出参数需要改为选中的所有列信息
            HashMap<String, String> paramAndTypeMap = new HashMap<>();
            dataSourceApiParams.getSearchColumns().forEach(column -> {
                paramAndTypeMap.put(column.getColumnAlias(), column.getColumnType());
            });
            interfaceInfo.setRequestParams(JSON.toJSONString(paramAndTypeMap));
//        数据源api 按照创建用户的id为每个用户生成一个调用的token,这样就可以保证不同用户之间可以有重复的接口
//            调用的时候先按照token解析出对应的用户，然后根据接口url判断是这个用户的哪个接口然后调用
            String datasourceToken = DatasourceApiTokenUtils.generateToken(interfaceInfo.getUserId().toString());
            ApiTokenRel apiTokenRel = new ApiTokenRel();
            apiTokenRel.setInterfaceId(interfaceInfo.getId());
            apiTokenRel.setToken(datasourceToken);
            apiTokenRel.setUserId(interfaceInfo.getUserId());
            apiTokenRelService.save(apiTokenRel);
            saveOrUpdateInterface(interfaceInfo);
        }

//        刷新网关的缓存接口数据
        updateGatewayCache();

        return interfaceInfo.getId();
    }

    @Override
    @Cacheable(cacheNames = RedisConstant.INTERFACE_PAGE_CACHE_NAMES, key = "#root.args[0].current+'_'+#root.args[0].pageSize+'_'+T(java.util.Objects).hash(#root.args[0])")
    public Page<Interfaceinfo> selectInterfaceInfoByPage(InterfaceInfoQueryBaseDto interfaceInfoQueryRequest) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();

        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
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
        queryWrapper.eq(interfaceInfoQueryRequest.getInterfaceType() != null, "interfaceType", interfaceInfoQueryRequest.getInterfaceType());
        queryWrapper.eq(interfaceInfoQueryRequest.getStatus() != null, "status", interfaceInfoQueryRequest.getStatus());
        return this.page(new Page<>(current, size), queryWrapper);
    }


    @Override
    @CacheEvict(cacheNames = RedisConstant.INTERFACE_PAGE_CACHE_NAMES, allEntries = true)
    public boolean updateInterfaceInfo(User user, Interfaceinfo interfaceInfo) {
        if (interfaceInfo == null || interfaceInfo.getId() == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "接口数据或id不能为空");
        }
        validInterfaceInfo(interfaceInfo.getId(), user);
//        判断数据库中是否有重复的uri
        Interfaceinfo dbInterfaceInfo = this.getOne(
                Wrappers.<Interfaceinfo>lambdaQuery()
                        .eq(Interfaceinfo::getUri, interfaceInfo.getUri())
                        .ne(Interfaceinfo::getId, interfaceInfo.getId()));
        if (dbInterfaceInfo != null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "接口地址不能重复");
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
    public Interfaceinfo validInterfaceInfo(Long interfaceInfoId, User user) {
        if (interfaceInfoId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "接口id不能为空");
        }
//        判断接口是否存在
        Interfaceinfo oldInterfaceInfo = this.getById(interfaceInfoId);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "接口不存在");
        }

        //判断用户是否有权限修改这个接口 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !"admin".equals(user.getUserRole())) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR);
        }
        return oldInterfaceInfo;
    }

    @Override
    @CacheEvict(cacheNames = RedisConstant.INTERFACE_PAGE_CACHE_NAMES, allEntries = true)
    public boolean updateInterfaceInfoStatus(UpdateStatusDto dto, User user) {

//        判断这个接口只能是管理员或接口拥有着可以修改
        Interfaceinfo interfaceinfo = this.validInterfaceInfo(dto.getInterfaceId(), user);

        if (!InterfaceInfoConstant.STATIC_NOT_USE.equals(interfaceinfo.getStatus()) && !InterfaceInfoConstant.STATIC_USE.equals(interfaceinfo.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "只能修改上下线的状态");
        }

        boolean result = lambdaUpdate().eq(Interfaceinfo::getId, dto.getInterfaceId())
                .set(Interfaceinfo::getStatus, dto.getStatus())
                .update();
//        boolean result = this.update(Wrappers.<Interfaceinfo>lambdaUpdate()
//                .eq(Interfaceinfo::getId, dto.getInterfaceId())
//                .set(Interfaceinfo::getStatus, dto.getStatus()));
        this.updateGatewayCache();
        return result;
    }


    @Override
    @CacheEvict(cacheNames = RedisConstant.INTERFACE_PAGE_CACHE_NAMES, allEntries = true)
    public Interfaceinfo saveOrUpdateInterface(Interfaceinfo interfaceinfo) {
        boolean result = this.saveOrUpdate(interfaceinfo);
        return interfaceinfo;
    }

    @Override
    public List<Interfaceinfo> listByDscId(Long dscId) {
        return lambdaQuery()
                .eq(Interfaceinfo::getDscId, dscId)
                .list();
    }

    /**
     * 校验uri是否重复 返回false表示没重复
     *
     * @param dto
     * @return
     */
    @Override
    public Boolean checkName(CheckNameDto dto) {
        List<Interfaceinfo> interfaceInfos = lambdaQuery()
                .eq(Interfaceinfo::getUri, dto.getName())
                .list();

        if (interfaceInfos.isEmpty()) {
            return false;
        }

        if (dto.getId() != null) {
            Interfaceinfo interfaceinfo = interfaceInfos.get(0);
            return interfaceinfo.getId().equals(dto.getId());
        }
        return false;
    }

    @Override
    public List<Interfaceinfo> listByInterfaceType(InterfaceTypeEnum interfaceTypeEnum) {
        return lambdaQuery()
                .eq(Interfaceinfo::getInterfaceType, interfaceTypeEnum)
                .list();
    }


    private Boolean haveParamsOrBody(List<GRequestParamsDto> getRequestParams, String requestParams) {

//        如果全是为空的，那么就直接返回false。如果返回true，那么就是有其中一个

        return (requestParams != null || (getRequestParams != null && !getRequestParams.isEmpty()));
    }

}




