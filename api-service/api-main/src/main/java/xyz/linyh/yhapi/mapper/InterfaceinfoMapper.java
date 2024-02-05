package xyz.linyh.yhapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.linyh.model.interfaceinfo.InterfaceAllCountAndCallCount;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;

/**
 * @author lin
 * @description 针对表【interfaceinfo(接口信息表)】的数据库操作Mapper
 * @createDate 2023-09-03 19:31:19
 * @Entity xyz.linyh.yhapi.model.entity.Interfaceinfo
 */
public interface InterfaceinfoMapper extends BaseMapper<Interfaceinfo> {


    /**
     * 获取某一个用户的所有可用接口数量和调用次数
     * @param id 接口id
     * @return
     */
    InterfaceAllCountAndCallCount getAllInterCountAndCallCount(@Param("id") Long id);
}




