package com.rosy.web.controller.main;

import com.rosy.common.annotation.ValidateRequest;
import com.rosy.common.domain.entity.ApiResponse;
import com.rosy.common.domain.entity.IdRequest;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.main.domain.dto.product.*;
import com.rosy.main.domain.vo.*;
import com.rosy.main.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@Tag(name = "商品管理")
public class ProductController {

    @Resource
    private IProductService productService;

    @PostMapping("/create")
    @ValidateRequest
    @Operation(summary = "创建商品")
    public ApiResponse createProduct(@RequestBody ProductCreateRequest request) {
        return ApiResponse.success(productService.createProduct(request));
    }

    @PostMapping("/update")
    @ValidateRequest
    @Operation(summary = "更新商品")
    public ApiResponse updateProduct(@RequestBody ProductUpdateRequest request) {
        if (request.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(productService.updateProduct(request));
    }

    @PostMapping("/delete")
    @ValidateRequest
    @Operation(summary = "删除商品")
    public ApiResponse deleteProduct(@RequestBody IdRequest request) {
        return ApiResponse.success(productService.removeById(request.getId()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取商品详情")
    public ApiResponse getProductDetail(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(productService.getProductDetail(id));
    }

    @PostMapping("/list")
    @ValidateRequest
    @Operation(summary = "获取商品列表")
    public ApiResponse getProductList(@RequestBody ProductQueryRequest request) {
        return ApiResponse.success(productService.getProductList(request));
    }
}
