package xyz.linyh.yhapi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.model.interfaceinfo.InterfaceInfoInvokePayType;
import xyz.linyh.model.interfaceinfo.vo.InterfaceInfoVO;
import xyz.linyh.model.userinterfaceinfo.entitys.UserInterfaceinfo;

import java.util.List;
import java.util.Map;

/**
 * @author lin
 * @description 针对表【userinterfaceinfo(用户接口调用次数关系表)】的数据库操作Service
 * @createDate 2023-09-11 21:20:10
 */
public interface UserinterfaceinfoService extends IService<UserInterfaceinfo> {

    void validInterfaceInfo(UserInterfaceinfo userInterfaceinfo, boolean add);

    public BaseResponse invokeOk(Long interfaceInfoId, Long userId,InterfaceInfoInvokePayType payType);

    /**
     * 判断某个用户是否有次数调用某个接口或是否有权限调用某个接口
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    InterfaceInfoInvokePayType isInvokeAndGetPayType(Long interfaceInfoId, Long userId, Integer pointsRequired);

    /**
     * 获取所有接口的调用次数前xx的数据
     *
     * @return
     */
    List<InterfaceInfoVO> analyzeAllInterfaceInfo(Integer current, Integer total,String status);


    /**
     * 获取某一个用户所有接口的调用次数前xx的数据
     * @param current'当前页
     * @return
     */
    List<InterfaceInfoVO> analyzeSelfInterfaceInfo(Integer current, Integer total,Long userId,String status);

    /**
     * 获取用户自己接口的调用次数前5的数据
     *
     * @param id
     * @return
     */
    BaseResponse<List<InterfaceInfoVO>> analyzeSelfInterfaceInfo(Long id);

    /**
     * 根据接口id获取接口的所有信息（包括调用总次数）
     *
     * @param interfaceId
     * @return
     */
    BaseResponse<InterfaceInfoVO> getInterfaceAllDataByInterfaceId(Long interfaceId, Long userId);


    /**
     * 增加某个的接口调用总数
     *
     * @param interfaceId
     * @param userId
     * @param count
     * @return
     */
    Boolean addCountIfNo(Long interfaceId, Long userId, Integer count);

    /**
     * 获取接口详细信息，对应用户剩下多少调用次数
     *
     * @param userId
     * @param interfaceId
     * @return
     */
    InterfaceInfoVO getInterfaceWithRemNumByInterfaceId(Long userId, Long interfaceId);

    /**
     * 获取用户剩余调用次数
     * @param id
     * @param interfaceId
     * @return
     */
    Integer getInterfaceRemCount(Long id, Long interfaceId);

    /**
     * 查询有调用次数的所有接口和对应调用次数
     * @return
     */
    Map<Long, Integer> listAllInterfaceCallCount();

    /**
     * 批量对所有接口增加调用次数，如果不存在就创建
     * @param allInterfaceUserAddCount
     * @return
     */
    boolean batchAddUserInterfaceCallCount(Map<Map<Long,Long>,Integer> allInterfaceUserAddCount);
}
