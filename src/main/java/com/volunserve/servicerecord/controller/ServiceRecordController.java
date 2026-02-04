package com.volunserve.servicerecord.controller;

import com.volunserve.common.response.Result;
import com.volunserve.servicerecord.dto.ServiceRecordDTO;
import com.volunserve.servicerecord.dto.ServiceRecordQueryDTO;
import com.volunserve.servicerecord.dto.ServiceReviewDTO;
import com.volunserve.servicerecord.service.ServiceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/service-records")
@RequiredArgsConstructor
@Tag(name = "服务记录管理", description = "志愿服务记录管理接口")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    @PostMapping("/from-registration/{registrationId}")
    @Operation(summary = "从报名记录创建服务记录")
    public Result<ServiceRecordDTO> createServiceRecordFromRegistration(@PathVariable Long registrationId) {
        return Result.success(serviceRecordService.createServiceRecordFromRegistration(registrationId));
    }

    @PostMapping("/review")
    @Operation(summary = "提交服务评价")
    public Result<ServiceRecordDTO> submitReview(@Valid @RequestBody ServiceReviewDTO dto) {
        return Result.success(serviceRecordService.submitReview(dto));
    }

    @PostMapping("/{id}/certificate")
    @Operation(summary = "生成服务证明")
    public Result<ServiceRecordDTO> generateCertificate(@PathVariable Long id) {
        return Result.success(serviceRecordService.generateCertificate(id));
    }

    @GetMapping("/{id}/certificate/content")
    @Operation(summary = "获取服务证明内容")
    public Result<String> getCertificateContent(@PathVariable Long id) {
        return Result.success(serviceRecordService.getCertificateContent(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取服务记录详情")
    public Result<ServiceRecordDTO> getServiceRecord(@PathVariable Long id) {
        return Result.success(serviceRecordService.getServiceRecord(id));
    }

    @GetMapping
    @Operation(summary = "分页查询服务记录")
    public Result<Page<ServiceRecordDTO>> getServiceRecords(ServiceRecordQueryDTO queryDTO) {
        return Result.success(serviceRecordService.getServiceRecords(queryDTO));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的服务记录列表")
    public Result<List<ServiceRecordDTO>> getServiceRecordsByUser(@PathVariable Long userId) {
        return Result.success(serviceRecordService.getServiceRecordsByUser(userId));
    }

    @GetMapping("/user/{userId}/total-hours")
    @Operation(summary = "获取用户的总服务时长")
    public Result<BigDecimal> getTotalServiceHoursByUser(@PathVariable Long userId) {
        return Result.success(serviceRecordService.getTotalServiceHoursByUser(userId));
    }
}
