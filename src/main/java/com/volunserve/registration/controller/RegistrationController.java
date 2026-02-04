package com.volunserve.registration.controller;

import com.volunserve.common.response.Result;
import com.volunserve.registration.dto.RegistrationApproveDTO;
import com.volunserve.registration.dto.RegistrationCreateDTO;
import com.volunserve.registration.dto.RegistrationDTO;
import com.volunserve.registration.dto.RegistrationQueryDTO;
import com.volunserve.registration.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
@Tag(name = "报名管理", description = "志愿者报名管理接口")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @Operation(summary = "创建报名")
    public Result<RegistrationDTO> createRegistration(@Valid @RequestBody RegistrationCreateDTO dto) {
        return Result.success(registrationService.createRegistration(dto));
    }

    @PostMapping("/approve")
    @Operation(summary = "审核报名")
    public Result<RegistrationDTO> approveRegistration(@Valid @RequestBody RegistrationApproveDTO dto) {
        return Result.success(registrationService.approveRegistration(dto));
    }

    @PostMapping("/{id}/checkin")
    @Operation(summary = "签到")
    public Result<RegistrationDTO> checkIn(@PathVariable Long id) {
        return Result.success(registrationService.checkIn(id));
    }

    @PostMapping("/{id}/checkout")
    @Operation(summary = "签退")
    public Result<RegistrationDTO> checkOut(@PathVariable Long id) {
        return Result.success(registrationService.checkOut(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消报名")
    public Result<Void> cancelRegistration(@PathVariable Long id) {
        registrationService.cancelRegistration(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取报名详情")
    public Result<RegistrationDTO> getRegistration(@PathVariable Long id) {
        return Result.success(registrationService.getRegistration(id));
    }

    @GetMapping
    @Operation(summary = "分页查询报名列表")
    public Result<Page<RegistrationDTO>> getRegistrations(RegistrationQueryDTO queryDTO) {
        return Result.success(registrationService.getRegistrations(queryDTO));
    }

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "获取活动的报名列表")
    public Result<List<RegistrationDTO>> getRegistrationsByActivity(@PathVariable Long activityId) {
        return Result.success(registrationService.getRegistrationsByActivity(activityId));
    }
}
