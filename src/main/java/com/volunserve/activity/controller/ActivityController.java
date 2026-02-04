package com.volunserve.activity.controller;

import com.volunserve.activity.dto.ActivityCreateDTO;
import com.volunserve.activity.dto.ActivityDTO;
import com.volunserve.activity.dto.ActivityQueryDTO;
import com.volunserve.activity.dto.ActivityUpdateDTO;
import com.volunserve.activity.enumeration.ActivityStatus;
import com.volunserve.activity.service.ActivityService;
import com.volunserve.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
@Tag(name = "活动管理", description = "活动的增删改查接口")
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @Operation(summary = "创建活动")
    public Result<ActivityDTO> createActivity(@Valid @RequestBody ActivityCreateDTO dto) {
        return Result.success(activityService.createActivity(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新活动")
    public Result<ActivityDTO> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateDTO dto) {
        return Result.success(activityService.updateActivity(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取活动详情")
    public Result<ActivityDTO> getActivity(@PathVariable Long id) {
        return Result.success(activityService.getActivityById(id));
    }

    @GetMapping
    @Operation(summary = "分页查询活动列表")
    public Result<Page<ActivityDTO>> getActivities(ActivityQueryDTO queryDTO) {
        return Result.success(activityService.getActivities(queryDTO));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "获取即将开始的活动")
    public Result<List<ActivityDTO>> getUpcomingActivities() {
        return Result.success(activityService.getUpcomingActivities());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新活动状态")
    public Result<Void> updateActivityStatus(
            @PathVariable Long id,
            @RequestParam ActivityStatus status) {
        activityService.updateActivityStatus(id, status);
        return Result.success();
    }
}
