package xyz.linyh.pay.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.linyh.dubboapi.service.DubboUserService;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.AlipayConstant;
import xyz.linyh.ducommon.constant.PayConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.pay.dto.CreateCreditOrderDto;
import xyz.linyh.model.pay.eneity.CreditOrder;
import xyz.linyh.model.pay.eneity.CreditProducts;
import xyz.linyh.pay.mapper.CreditOrderMapper;
import xyz.linyh.pay.service.CreditOrderService;
import xyz.linyh.pay.service.CreditProductsService;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * @author lin
 * @description 针对表【creditOrder(积分订单表)】的数据库操作Service实现
 * @createDate 2024-03-04 20:47:55
 */
@Service
public class CreditorderServiceImpl extends ServiceImpl<CreditOrderMapper, CreditOrder>
        implements CreditOrderService {

    @Autowired
    private CreditProductsService creditProductsService;

    @Autowired
    private DubboUserService dubboUserService;


    @Override
    public CreditOrder createCreditOrder(CreateCreditOrderDto dto, Long userId) {
//        0. 参数校验
        if (dto == null || dto.getProductId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建订单需要携带产品id等等参数");
        }
        if (dto.getNum() < 1 || dto.getNum() >= 999) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建订单需要携带数量,并且需要合法");
        }

//        1. 获取商品判断商品是否存在
        CreditProducts creditProduct = creditProductsService.getById(dto.getProductId());
        if (creditProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }

//        2. 根据请求参数创建订单数据
        CreditOrder creditOrder = new CreditOrder();
        creditOrder.setOrderNo(getOrderId());
        creditOrder.setUserId(userId);
        creditOrder.setProductId(dto.getProductId());
        creditOrder.setOrderName(creditProduct.getDescription() + "* " + dto.getNum());
        creditOrder.setTotal(dto.getNum() * creditProduct.getPrice());
        creditOrder.setStatus(PayConstant.ORDER_STATIC_UNPAID);
        creditOrder.setPayType(dto.getPayType().toString());
        creditOrder.setProductInfo(creditProduct.getDescription());
        creditOrder.setAddPoints(((long) creditProduct.getIntegral() * dto.getNum()));
        creditOrder.setExpirationTime(LocalDateTime.now().plusDays(1L).toEpochSecond(java.time.ZoneOffset.of("+8")));
        creditOrder.setCreateTime(LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.of("+8")));
        creditOrder.setUpdateTime(LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.of("+8")));
        boolean saveResult = this.save(creditOrder);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建订单失败");
        }
        return creditOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatusAndOpt(Map<String, String> params) {
//        0. 参数校验
        if (params == null || params.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新订单状态需要携带参数");
        }

        String orderNum = params.get(AlipayConstant.ALI_PAY_ORDER_NUM);
        CreditOrder creditOrder = this.getOne(Wrappers.<CreditOrder>lambdaQuery().eq(CreditOrder::getOrderNo, orderNum));
        if (creditOrder == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单不存在");
        }

        Long orderTotal = creditOrder.getTotal();
        String strPrice = params.get(AlipayConstant.ALI_PAY_ORDER_TOTAL);
        int intPrice = Integer.parseInt(strPrice)*100;
        if (orderTotal != intPrice) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单金额不一致");
        }

        if (params.get(AlipayConstant.ALI_PAY_ORDER_RESULT).equals(AlipayConstant.PAY_RESULT_SUCCESS)) {
//            更新订单状态
            creditOrder.setStatus(PayConstant.ORDER_STATIC_PAID);
            this.updateById(creditOrder);
//            增加用户积分
            boolean addCreditResult = dubboUserService.addUserCredit(creditOrder.getUserId(), Integer.parseInt(creditOrder.getAddPoints().toString()));
            if (!addCreditResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加用户积分失败");
            }
        } else {
            log.error("支付失败");
        }

        return true;
    }

    @Override
    public Page<CreditOrder> listAllCreditOrderByPage(Integer current, Integer pageSize,Long userId) {
        Page<CreditOrder> page = new Page<CreditOrder>(current, pageSize);
        return this.page(page, Wrappers.<CreditOrder>lambdaQuery().eq(CreditOrder::getUserId,userId).orderByDesc(CreditOrder::getCreateTime));
    }

    @Override
    public CreditOrder getCreditOrderById(String id, Long userId) {
        return this.getOne(Wrappers.<CreditOrder>lambdaQuery().eq(CreditOrder::getId, id).eq(CreditOrder::getUserId, userId));
    }

    @Override
    public void deleteCreditOrderByUserIdAndId(String id, Long userId) {
        this.remove(Wrappers.<CreditOrder>lambdaQuery().eq(CreditOrder::getId, id).eq(CreditOrder::getUserId, userId));
    }

    /**
     * 根据雪花算法生成订单id
     *
     * @return
     */
    private String getOrderId() {
        Snowflake snowflake = IdUtil.getSnowflake();
        return snowflake.nextIdStr();
    }
}




