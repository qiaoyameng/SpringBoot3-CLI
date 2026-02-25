package com.volunserve.controller;

import com.volunserve.dto.*;
import com.volunserve.entity.enums.RegistrationStatus;
import com.volunserve.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<RegistrationDTO>> register(
            @Valid @RequestBody RegistrationCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long volunteerId = Long.valueOf(userDetails.getUsername());
        RegistrationDTO registration = registrationService.register(dto, volunteerId);
        return ResponseEntity.ok(ApiResponse.success("报名成功", registration));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<RegistrationDTO>> reviewRegistration(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationReviewDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long reviewerId = Long.valueOf(userDetails.getUsername());
        RegistrationDTO registration = registrationService.reviewRegistration(id, dto, reviewerId);
        return ResponseEntity.ok(ApiResponse.success("审核完成", registration));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long volunteerId = Long.valueOf(userDetails.getUsername());
        registrationService.cancelRegistration(id, volunteerId);
        return ResponseEntity.ok(ApiResponse.success("报名已取消", null));
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<RegistrationDTO>> checkIn(@Valid @RequestBody CheckInDTO dto) {
        RegistrationDTO registration = registrationService.checkIn(dto);
        return ResponseEntity.ok(ApiResponse.success("签到成功", registration));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<RegistrationDTO>> checkOut(@Valid @RequestBody CheckInDTO dto) {
        RegistrationDTO registration = registrationService.checkOut(dto);
        return ResponseEntity.ok(ApiResponse.success("签出成功", registration));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistrationDTO>> getRegistration(@PathVariable Long id) {
        RegistrationDTO registration = registrationService.getRegistration(id);
        return ResponseEntity.ok(ApiResponse.success(registration));
    }

    @GetMapping("/my-registrations")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<PageResult<RegistrationDTO>>> getMyRegistrations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long volunteerId = Long.valueOf(userDetails.getUsername());
        PageResult<RegistrationDTO> registrations = registrationService.getMyRegistrations(volunteerId, page, size);
        return ResponseEntity.ok(ApiResponse.success(registrations));
    }

    @GetMapping("/activity/{activityId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResult<RegistrationDTO>>> getActivityRegistrations(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<RegistrationDTO> registrations = registrationService.getActivityRegistrations(activityId, page, size);
        return ResponseEntity.ok(ApiResponse.success(registrations));
    }

    @GetMapping("/activity/{activityId}/status/{status}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<RegistrationDTO>>> getActivityRegistrationsByStatus(
            @PathVariable Long activityId,
            @PathVariable RegistrationStatus status) {
        List<RegistrationDTO> registrations = registrationService.getActivityRegistrationsByStatus(activityId, status);
        return ResponseEntity.ok(ApiResponse.success(registrations));
    }

    @GetMapping("/activity/{activityId}/my-registration")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<RegistrationDTO>> getMyRegistrationByActivity(
            @PathVariable Long activityId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long volunteerId = Long.valueOf(userDetails.getUsername());
        RegistrationDTO registration = registrationService.getRegistrationByActivityAndVolunteer(activityId, volunteerId);
        return ResponseEntity.ok(ApiResponse.success(registration));
    }
}
