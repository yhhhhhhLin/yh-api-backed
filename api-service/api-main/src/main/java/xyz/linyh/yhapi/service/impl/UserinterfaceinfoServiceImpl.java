package xyz.linyh.yhapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.UserInterfaceInfoConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.interfaceinfo.vo.InterfaceInfoVO;
import xyz.linyh.model.userinterfaceinfo.entitys.UserInterfaceinfo;
import xyz.linyh.yhapi.mapper.UserinterfaceinfoMapper;
import xyz.linyh.yhapi.service.InterfaceinfoService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* @author lin
* @description 针对表【userinterfaceinfo(用户接口调用次数关系表)】的数据库操作Service实现
* @createDate 2023-09-11 21:20:10
*/
@DubboService
@Slf4j
public class UserinterfaceinfoServiceImpl extends ServiceImpl<UserinterfaceinfoMapper, UserInterfaceinfo>
    implements UserinterfaceinfoService {

    @Resource
    private UserinterfaceinfoMapper userinterfaceinfoMapper;

    @Resource
    private InterfaceinfoService interfaceinfoService;



    /**
     * 对接口信息进行校验
     *
     * @param userInterfaceinfo
     * @param add
     */
    @Override
    public void validInterfaceInfo(UserInterfaceinfo userInterfaceinfo, boolean add) {
        if (userInterfaceinfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = userInterfaceinfo.getId();
        Long userId = userInterfaceinfo.getUserId();
        Integer remNum = userInterfaceinfo.getRemNum();
        Integer status = userInterfaceinfo.getStatus();
        Integer allNum = userInterfaceinfo.getAllNum();


        // 创建时，所有参数必须非空
        if (add) {
            if (userId <=0 || remNum<0 || allNum<0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (id==null || id<=0 || userId <=0 || remNum<0 || allNum<0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
//        if ((status!=0 && status!=1)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }

    }

//    todo 调用成功，调用次数+1，可调用次数-1

    /**
     * 调用成功 调用次数+1，可调用次数-1
     * @param interfaceInfoId
     * @return
     */
    @Override
    public BaseResponse invokeOk(Long interfaceInfoId, Long userId){
        if(interfaceInfoId==null || userId==null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"确实接口id或用户id");
        }
//        调用次数+1，可调用次数-1
        LambdaUpdateWrapper<UserInterfaceinfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInterfaceinfo::getUserId,userId)
                        .eq(UserInterfaceinfo::getInterfaceId,interfaceInfoId)
                                .setSql("remNum = remNum-1,allNum = allNum+1");
        boolean update = this.update(wrapper);
        return ResultUtils.success(update);
    }

    /**
     * 判断某个用户是否有次数调用某个接口或是否有权限调用某个接口
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public Boolean isInvoke(Long interfaceInfoId, Long userId) {
        if(interfaceInfoId==null || userId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LambdaQueryWrapper<UserInterfaceinfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterfaceinfo::getInterfaceId,interfaceInfoId)
                .eq(UserInterfaceinfo::getUserId,userId);

        UserInterfaceinfo userInterfaceinfo = this.getOne(wrapper);
        if(userInterfaceinfo==null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"您没有调用次数，请先获取调用次数");
        }

        Integer remNum = userInterfaceinfo.getRemNum();
        Integer status = userInterfaceinfo.getStatus();
        if(remNum<=0 || !status.equals(UserInterfaceInfoConstant.CAN_USE)){
            log.info("{}没有次数或无法调用这个{}接口",userId,interfaceInfoId);
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"您没有调用次数，请先获取调用次数");
        }
        return true;
    }

    /**
     * 获取所有接口的调用次数前5的数据
     *
     * @return
     */
    @Override
    public BaseResponse<List<InterfaceInfoVO>> analyzeInterfaceInfo() {
//       todo 获取每个接口调用次数 目前只获取调用次数前5的
        List<UserInterfaceinfo> interfaceCount = userinterfaceinfoMapper.getInterfaceCount(5);
        if(interfaceCount==null || interfaceCount.size()==0){
            ResultUtils.success("无数据");
        }

        Map<Long, List<UserInterfaceinfo>> collectGroupBy = interfaceCount.stream().collect(Collectors.groupingBy(UserInterfaceinfo::getInterfaceId));

        List<Interfaceinfo> list = interfaceinfoService.list(Wrappers.<Interfaceinfo>lambdaQuery().in(Interfaceinfo::getId, collectGroupBy.keySet()));

        List<InterfaceInfoVO> interfaceInfoVOS = list.stream().map(interfaceinfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceinfo, interfaceInfoVO);
            for (Long interfaceId : collectGroupBy.keySet()) {
                if (interfaceId.equals(interfaceinfo.getId())) {
                    interfaceInfoVO.setAllNum(collectGroupBy.get(interfaceId).get(0).getAllNum());
                }
            }

            return interfaceInfoVO;
        }).collect(Collectors.toList());

//        todo
        List<InterfaceInfoVO> orderInterfaceInfoVOS = interfaceInfoVOS.stream().sorted(Comparator.comparing(InterfaceInfoVO::getAllNum).reversed()).collect(Collectors.toList());

        return ResultUtils.success(orderInterfaceInfoVOS);
    }


    /**
     * 获取用户自己接口的调用次数前5的数据
     * @param id
     * @return
     */
    @Override
    public BaseResponse<List<InterfaceInfoVO>> analyzeSelfInterfaceInfo(Long id) {
//        获取每一个接口对应的总调用次数
        List<InterfaceInfoVO> interfaceCount = userinterfaceinfoMapper.getSelfInterfaceCount(5,id);

        return ResultUtils.success(interfaceCount);


////        根据interfaceinfoId一个一个去查对应的接口详细信息
//        List<InterfaceInfoVO> orderInterfaceInfoVOS = interfaceCount.stream().map(userInterfaceInfo  -> {
//
//            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
//            BeanUtils.copyProperties(userInterfaceInfo, interfaceInfoVO);
//            Long interfaceId = userInterfaceInfo.getInterfaceId();
//            Interfaceinfo interfaceinfo = interfaceinfoService.getById(interfaceId);
//            BeanUtils.copyProperties(interfaceinfo,interfaceInfoVO);
//
//            return interfaceInfoVO;
//        }).collect(Collectors.toList());
//
//        return ResultUtils.success(orderInterfaceInfoVOS);
    }

    /**
     * 根据接口id获取接口的所有信息（包括调用总次数）
     *
     * @param interfaceId
     * @return
     */
    @Override
    public BaseResponse<InterfaceInfoVO> getInterfaceAllDataByInterfaceId(Long interfaceId) {
        InterfaceInfoVO interfaceInfoVO = userinterfaceinfoMapper.getInterfaceCountByInterfaceId(interfaceId);
        return ResultUtils.success(interfaceInfoVO);
    }


    /**
     * 对应用户没有相关接口的调用次数，创建对应一条数据
     *
     * @param interfaceId
     * @param userId
     * @param count
     * @return
     */
    @Override
    public Boolean addCountIfNo(Long interfaceId, Long userId, Integer count) {
        if(interfaceId==null || userId==null || count==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"增加次数相关参数不能为可能");
        }

        UserInterfaceinfo userInterfaceinfo = new UserInterfaceinfo();
        userInterfaceinfo.setUserId(userId);
        userInterfaceinfo.setInterfaceId(interfaceId);
        userInterfaceinfo.setRemNum(count);

        boolean save = this.save(userInterfaceinfo);
        return null;
    }

    /**
     * 获取接口详细信息，对应用户剩下多少调用次数
     *
     * @param userId
     * @param interfaceId
     * @return
     */
    @Override
    public InterfaceInfoVO getInterfaceWithRemNumByInterfaceId(Long userId, Long interfaceId) {

        InterfaceInfoVO userinterfaceinfo = userinterfaceinfoMapper.getInterfaceCountByInterfaceId(interfaceId);
        return userinterfaceinfo;
    }

}




