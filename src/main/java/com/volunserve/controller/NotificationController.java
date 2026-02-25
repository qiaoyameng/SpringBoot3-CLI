package com.volunserve.controller;

import com.volunserve.dto.ApiResponse;
import com.volunserve.dto.NotificationDTO;
import com.volunserve.dto.PageResult;
import com.volunserve.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResult<NotificationDTO>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = Long.valueOf(userDetails.getUsername());
        PageResult<NotificationDTO> notifications = notificationService.getMyNotifications(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success("查询成功", count));
    }

    @PostMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("已标记为已读", null));
    }

    @PostMapping("/read-all")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("全部标记为已读", null));
    }
}
