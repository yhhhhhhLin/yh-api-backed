package xyz.linyh.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.linyh.model.pay.eneity.UserCredits;

/**
* @author lin
* @description 针对表【usercredits(用户剩余积分表)】的数据库操作Mapper
* @createDate 2023-12-26 13:51:29
* @Entity generator.domain.Usercredits
*/
@Mapper
public interface UserCreditsMapper extends BaseMapper<UserCredits> {

}




