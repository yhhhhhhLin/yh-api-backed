package xyz.linyh.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.pay.dto.CreateCreditOrderDto;
import xyz.linyh.model.pay.eneity.CreditOrder;

import java.util.Map;

/**
* @author lin
* @description 针对表【creditOrder(积分订单表)】的数据库操作Service
* @createDate 2024-03-04 20:47:55
*/
public interface CreditOrderService extends IService<CreditOrder> {

    /**
     * 创建积分订单信息
     * @return
     */
    CreditOrder createCreditOrder(CreateCreditOrderDto dto,Long userId);

    /**
     * 更新对应订单状态,和后续操作
     * @param params
     * @return
     */
    boolean updateOrderStatusAndOpt(Map<String, String> params);
}
