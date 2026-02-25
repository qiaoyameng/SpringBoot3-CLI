package com.volunserve.controller;

import com.lowagie.text.DocumentException;
import com.volunserve.dto.*;
import com.volunserve.service.ServiceRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/service-records")
@RequiredArgsConstructor
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    @PostMapping("/create/{registrationId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ServiceRecordDTO>> createServiceRecord(
            @PathVariable Long registrationId) {
        ServiceRecordDTO record = serviceRecordService.createServiceRecord(registrationId);
        return ResponseEntity.ok(ApiResponse.success("服务记录创建成功", record));
    }

    @PostMapping("/review")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ServiceRecordDTO>> submitReview(
            @Valid @RequestBody ReviewDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        ServiceRecordDTO record = serviceRecordService.submitReview(dto, userId);
        return ResponseEntity.ok(ApiResponse.success("评价提交成功", record));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceRecordDTO>> getServiceRecord(@PathVariable Long id) {
        ServiceRecordDTO record = serviceRecordService.getServiceRecord(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<ApiResponse<ServiceRecordDTO>> getServiceRecordByRegistration(
            @PathVariable Long registrationId) {
        ServiceRecordDTO record = serviceRecordService.getServiceRecordByRegistration(registrationId);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @GetMapping("/my-records")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<PageResult<ServiceRecordDTO>>> getMyServiceRecords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long volunteerId = Long.valueOf(userDetails.getUsername());
        PageResult<ServiceRecordDTO> records = serviceRecordService.getMyServiceRecords(volunteerId, page, size);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/activity/{activityId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ServiceRecordDTO>>> getActivityServiceRecords(
            @PathVariable Long activityId) {
        List<ServiceRecordDTO> records = serviceRecordService.getActivityServiceRecords(activityId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/my-total-hours")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<Integer>> getMyTotalServiceHours(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long volunteerId = Long.valueOf(userDetails.getUsername());
        Integer totalHours = serviceRecordService.getTotalServiceHours(volunteerId);
        return ResponseEntity.ok(ApiResponse.success("查询成功", totalHours));
    }

    @GetMapping("/my-record-count")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<Long>> getMyServiceRecordCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long volunteerId = Long.valueOf(userDetails.getUsername());
        long count = serviceRecordService.getServiceRecordCount(volunteerId);
        return ResponseEntity.ok(ApiResponse.success("查询成功", count));
    }

    @GetMapping("/{id}/certificate")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<byte[]> generateCertificate(@PathVariable Long id) throws IOException, DocumentException {
        byte[] pdfBytes = serviceRecordService.generateCertificate(id);
        serviceRecordService.markCertificateDownloaded(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "volunteer_certificate_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
