package xyz.linyh.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.pay.dto.CreditProductDto;
import xyz.linyh.model.pay.eneity.CreditProducts;
import xyz.linyh.pay.service.CreditProductsService;

import java.util.List;

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

    @PostMapping("/add")
    public BaseResponse<Boolean> addCreditProduct(@RequestBody CreditProductDto dto) {
        boolean result = creditProductsService.addCreditProduct(dto);
        return ResultUtils.success(result);
    }

}
