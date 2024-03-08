package xyz.linyh.pay.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.RedisConstant;
import xyz.linyh.model.pay.dto.CreditProductDto;
import xyz.linyh.model.pay.dto.UpdateCreditProductDto;
import xyz.linyh.model.pay.eneity.CreditProducts;
import xyz.linyh.pay.service.CreditProductsService;

import java.util.List;

/**
 * 对积分商品进行增上改查
 */
@RestController
@RequestMapping("/creditProduct")
public class CreditProductsController {


    @Autowired
    private CreditProductsService creditProductsService;

    /**
     * 获取所有积分商品
     * @return 所有积分商品
     */
    @GetMapping("/list")
    public BaseResponse<List<CreditProducts>> ListCreditProduct() {
        List<CreditProducts> creditProducts = creditProductsService.listAllCreditProduct();
        return ResultUtils.success(creditProducts);
    }

    /**
     * 增加积分商品
     * @param dto
     * @return
     */
    @PostMapping
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> addCreditProduct(@RequestBody CreditProductDto dto) {
        boolean result = creditProductsService.addCreditProduct(dto);
        return ResultUtils.success(result);
    }

    /**
     * 删除某一个积分商品 添加缓存
     * @param productId 积分商品id
     * @return
     */
    @DeleteMapping("/{productId}")
    @AuthCheck(mustRole = "admin")
    @CacheEvict(cacheNames = RedisConstant.CREDIT_PRODUCT_CACHE_NAMES,allEntries = true)
    public BaseResponse delProduct(@PathVariable String productId) {
        if (productId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        boolean result = creditProductsService.removeById(productId);

        if (result) {
            return ResultUtils.success("true");
        } else {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }

    }

    /**
     * 修改某一个积分商品
     * @param dto 对应参数
     * @return
     */
    @PutMapping
    @AuthCheck(mustRole = "admin")
    @CacheEvict(cacheNames = RedisConstant.CREDIT_PRODUCT_CACHE_NAMES,allEntries = true)
    public BaseResponse updateProduct(@RequestBody UpdateCreditProductDto dto) {

        if (dto == null || dto.getCreditId() == null || dto.getIntegral() == null || dto.getDescription() == null || dto.getPrice() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        CreditProducts creditProducts = new CreditProducts();
        CreditProducts oldCreditProducts = creditProductsService.getById(dto.getCreditId());
        BeanUtils.copyProperties(oldCreditProducts, creditProducts);
        BeanUtils.copyProperties(dto, creditProducts);
        creditProducts.setId(dto.getCreditId());

        boolean result = creditProductsService.updateById(creditProducts);


        if (result) {
            return ResultUtils.success("true");
        } else {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }

    }

}
