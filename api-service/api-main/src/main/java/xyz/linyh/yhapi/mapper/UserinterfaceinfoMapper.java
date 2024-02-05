package xyz.linyh.yhapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.linyh.model.interfaceinfo.vo.InterfaceInfoVO;
import xyz.linyh.model.userinterfaceinfo.entitys.UserInterfaceinfo;

import java.util.List;

/**
 * @author lin
 * @description 针对表【userinterfaceinfo(用户接口调用次数关系表)】的数据库操作Mapper
 * @createDate 2023-09-11 21:20:10
 * @Entity xyz.linyh.yhapi.model.entity.UserInterfaceinfo
 */
public interface UserinterfaceinfoMapper extends BaseMapper<UserInterfaceinfo> {

    /**
     * 降序查询出所有接口的调用总次数和对应接口id
     *
     * @param limit
     * @return
     */
    public List<InterfaceInfoVO> getInterfaceAnalyze(@Param("offset") int offset, @Param("limit") int limit,@Param("userId") Long userId, @Param("status") String status);

    /**
     * 降序查询出用户自己的接口的调用总次数和对应接口id
     *
     * @param limit
     * @param id
     * @return
     */
    public List<InterfaceInfoVO> getSelfInterfaceCount(@Param("limit") Integer limit, @Param("id") Long id);

    /**
     * 根据接口id获取一个接口的所有信息
     *
     * @param interfaceId
     */
    InterfaceInfoVO getInterfaceCountByInterfaceId(@Param("interfaceId") Long interfaceId, @Param("userId") Long userId);

    /**
     * 根据接口id和用户id获取接口详细信息和用户对应这个接口还有多少剩余次数
     *
     * @param interfaceId
     * @param userId
     * @return
     */
    InterfaceInfoVO getInterfaceCountByInterfaceIdAndUserId(@Param("interfaceId") Long interfaceId, @Param("userId") Long userId);
}




