package xyz.linyh.pay.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.model.pay.dto.UpdateCreditProductDto;
import xyz.linyh.model.pay.eneity.UserCredits;
import xyz.linyh.pay.service.CreditProductsService;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.PageRequest;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.pay.dto.CreditProductDto;
import xyz.linyh.model.pay.eneity.CreditProducts;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private CreditProductsService creditProductsService;


    @PostMapping
    @AuthCheck(mustRole = "admin")
    public BaseResponse addProduct(@RequestBody CreditProductDto dto) {
        if (dto == null || dto.getIntegral() == null || dto.getDescription() == null || dto.getPrice() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        dto.setPicture("TODO");

        boolean result = creditProductsService.addCreditProduct(dto);

        if (result) {
            return ResultUtils.success("true");
        } else {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }

    }

    @DeleteMapping("/{productId}")
    @AuthCheck(mustRole = "admin")
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

    @PutMapping
    @AuthCheck(mustRole = "admin")
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

    /**
     * 获取所有产品
     *
     * @param dto
     * @return
     */
    @GetMapping
    public BaseResponse ListCreditProduct(PageRequest dto) {
        List<CreditProducts> list = creditProductsService.list(Wrappers.<CreditProducts>lambdaQuery().orderByAsc(CreditProducts::getPrice));
        return ResultUtils.success(list);
    }


}
