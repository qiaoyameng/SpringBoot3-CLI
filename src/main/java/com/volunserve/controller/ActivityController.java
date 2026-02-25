package com.volunserve.controller;

import com.volunserve.dto.*;
import com.volunserve.entity.enums.ActivityCategory;
import com.volunserve.entity.enums.ActivityStatus;
import com.volunserve.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ActivityDTO>> createActivity(
            @Valid @RequestBody ActivityCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long organizerId = Long.valueOf(userDetails.getUsername());
        ActivityDTO activity = activityService.createActivity(dto, organizerId);
        return ResponseEntity.ok(ApiResponse.success("活动创建成功", activity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ActivityDTO>> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateDTO dto) {
        ActivityDTO activity = activityService.updateActivity(id, dto);
        return ResponseEntity.ok(ApiResponse.success("活动更新成功", activity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.success("活动删除成功", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDTO>> getActivity(@PathVariable Long id) {
        ActivityDTO activity = activityService.getActivity(id);
        return ResponseEntity.ok(ApiResponse.success(activity));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<ActivityDTO>>> listActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        PageResult<ActivityDTO> activities = activityService.listActivities(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResult<ActivityDTO>>> searchActivities(
            @RequestBody ActivityQueryDTO query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<ActivityDTO> activities = activityService.searchActivities(query, page, size);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getNearbyActivities(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        List<ActivityDTO> activities = activityService.getNearbyActivities(latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<PageResult<ActivityDTO>>> getActivitiesByCategory(
            @PathVariable ActivityCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<ActivityDTO> activities = activityService.getActivitiesByCategory(category, page, size);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @GetMapping("/my-activities")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResult<ActivityDTO>>> getMyActivities(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long organizerId = Long.valueOf(userDetails.getUsername());
        PageResult<ActivityDTO> activities = activityService.getActivitiesByOrganizer(organizerId, page, size);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateActivityStatus(
            @PathVariable Long id,
            @RequestParam ActivityStatus status) {
        activityService.updateActivityStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("活动状态更新成功", null));
    }
}
