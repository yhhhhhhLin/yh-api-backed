package xyz.linyh.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.pay.dto.CreditProductDto;
import xyz.linyh.model.pay.eneity.CreditProducts;

/**
 * @author lin
 * @description 针对表【creditproducts(积分商品表)】的数据库操作Service
 * @createDate 2023-12-26 13:51:22
 */
public interface CreditProductsService extends IService<CreditProducts> {

    /**
     * 增加一个积分商品
     *
     * @param dto
     * @return
     */
    boolean addCreditProduct(CreditProductDto dto);
}
