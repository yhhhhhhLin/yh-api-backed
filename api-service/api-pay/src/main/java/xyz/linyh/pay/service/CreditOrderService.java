package xyz.linyh.pay.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 查询所有积分订单分页
     *
     * @param current  当前页数
     * @param pageSize 每页显示条数
     * @return
     */
    Page<CreditOrder> listAllCreditOrderByPage(Integer current, Integer pageSize,Long userId);

    /**
     * 根据id查询积分订单信息
     * @param id
     * @return
     */
    CreditOrder getCreditOrderById(String id,Long userId);

    /**
     * 删除某一个订单
     * @param id
     * @param userId
     */
    void deleteCreditOrderByUserIdAndId(String id, Long userId);
}
