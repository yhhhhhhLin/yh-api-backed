package xyz.linyh.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.linyh.pay.mapper.ProductsOrderMapper;
import xyz.linyh.pay.service.ProductsOrderService;
import xyz.linyh.model.pay.eneity.ProductsOrder;

/**
 * @author lin
 * @description 针对表【productsorder(商品订单)】的数据库操作Service实现
 * @createDate 2023-12-26 13:51:26
 */
@Service
public class ProductsOrderServiceImpl extends ServiceImpl<ProductsOrderMapper, ProductsOrder>
        implements ProductsOrderService {

}




