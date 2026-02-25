package com.volunserve.controller;

import com.volunserve.dto.ApiResponse;
import com.volunserve.dto.UserDTO;
import com.volunserve.dto.UserUpdateDTO;
import com.volunserve.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @Valid @RequestBody UserUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        UserDTO user = userService.updateUser(userId, dto);
        return ResponseEntity.ok(ApiResponse.success("个人资料更新成功", user));
    }

    @PostMapping("/me/password")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        userService.updatePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
    }
}
