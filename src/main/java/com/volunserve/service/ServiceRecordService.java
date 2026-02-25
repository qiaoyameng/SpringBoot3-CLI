package com.volunserve.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.volunserve.dto.*;
import com.volunserve.entity.Activity;
import com.volunserve.entity.Registration;
import com.volunserve.entity.ServiceRecord;
import com.volunserve.entity.User;
import com.volunserve.entity.enums.CheckInStatus;
import com.volunserve.exception.BusinessException;
import com.volunserve.exception.ResourceNotFoundException;
import com.volunserve.repository.ActivityRepository;
import com.volunserve.repository.RegistrationRepository;
import com.volunserve.repository.ServiceRecordRepository;
import com.volunserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final RegistrationRepository registrationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Transactional
    public ServiceRecordDTO createServiceRecord(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("报名记录不存在"));

        if (registration.getCheckInStatus() != CheckInStatus.CHECKED_OUT) {
            throw new BusinessException("志愿者尚未完成签出，无法创建服务记录");
        }

        if (serviceRecordRepository.existsByRegistrationId(registrationId)) {
            throw new BusinessException("服务记录已存在");
        }

        ServiceRecord serviceRecord = ServiceRecord.builder()
                .registration(registration)
                .volunteer(registration.getVolunteer())
                .activity(registration.getActivity())
                .serviceHours(registration.getServiceHours())
                .serviceDate(registration.getCheckOutTime().toLocalDate().atStartOfDay())
                .certificateDownloaded(false)
                .build();

        ServiceRecord saved = serviceRecordRepository.save(serviceRecord);
        notificationService.sendServiceRecordCreatedNotification(saved);

        return convertToDTO(saved);
    }

    @Transactional
    public ServiceRecordDTO submitReview(ReviewDTO dto, Long userId) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(dto.getServiceRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("服务记录不存在"));

        if (Boolean.TRUE.equals(dto.getIsVolunteerReview())) {
            if (!serviceRecord.getVolunteer().getId().equals(userId)) {
                throw new BusinessException("无权评价该服务记录");
            }
            if (serviceRecord.getVolunteerRating() != null) {
                throw new BusinessException("您已评价过该服务记录");
            }
            serviceRecord.setVolunteerRating(dto.getRating());
            serviceRecord.setVolunteerFeedback(dto.getContent());
        } else {
            if (!serviceRecord.getActivity().getOrganizer().getId().equals(userId)) {
                throw new BusinessException("无权评价该服务记录");
            }
            if (serviceRecord.getOrganizerRating() != null) {
                throw new BusinessException("您已评价过该服务记录");
            }
            serviceRecord.setOrganizerRating(dto.getRating());
            serviceRecord.setOrganizerFeedback(dto.getContent());
        }

        ServiceRecord saved = serviceRecordRepository.save(serviceRecord);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public ServiceRecordDTO getServiceRecord(Long id) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("服务记录不存在"));
        return convertToDTO(serviceRecord);
    }

    @Transactional(readOnly = true)
    public ServiceRecordDTO getServiceRecordByRegistration(Long registrationId) {
        ServiceRecord serviceRecord = serviceRecordRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("服务记录不存在"));
        return convertToDTO(serviceRecord);
    }

    @Transactional(readOnly = true)
    public PageResult<ServiceRecordDTO> getMyServiceRecords(Long volunteerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ServiceRecord> recordPage = serviceRecordRepository.findByVolunteerId(volunteerId, pageable);
        return convertToPageResult(recordPage);
    }

    @Transactional(readOnly = true)
    public List<ServiceRecordDTO> getActivityServiceRecords(Long activityId) {
        List<ServiceRecord> records = serviceRecordRepository.findByActivityId(activityId);
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getTotalServiceHours(Long volunteerId) {
        Integer total = serviceRecordRepository.sumServiceHoursByVolunteerId(volunteerId);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public long getServiceRecordCount(Long volunteerId) {
        return serviceRecordRepository.countByVolunteerId(volunteerId);
    }

    @Transactional
    public byte[] generateCertificate(Long serviceRecordId) throws IOException, DocumentException {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(serviceRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("服务记录不存在"));

        User volunteer = serviceRecord.getVolunteer();
        Activity activity = serviceRecord.getActivity();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);
        document.open();

        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(bfChinese, 24, Font.BOLD);
        Font subtitleFont = new Font(bfChinese, 16, Font.NORMAL);
        Font contentFont = new Font(bfChinese, 14, Font.NORMAL);
        Font signatureFont = new Font(bfChinese, 12, Font.NORMAL);

        Paragraph title = new Paragraph("志愿服务证明", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        Paragraph content = new Paragraph();
        content.setFont(contentFont);
        content.setLeading(25);
        content.add("兹证明 ");
        Chunk nameChunk = new Chunk(volunteer.getRealName() != null ? volunteer.getRealName() : volunteer.getUsername());
        nameChunk.setFont(new Font(bfChinese, 14, Font.BOLD));
        content.add(nameChunk);
        content.add(" 同志于 ");
        content.add(activity.getStartTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        content.add(" 参加了 ");
        Chunk activityChunk = new Chunk(activity.getTitle());
        activityChunk.setFont(new Font(bfChinese, 14, Font.BOLD));
        content.add(activityChunk);
        content.add(" 志愿服务活动，累计服务时长 ");
        Chunk hoursChunk = new Chunk(serviceRecord.getServiceHours() + " 小时");
        hoursChunk.setFont(new Font(bfChinese, 14, Font.BOLD));
        content.add(hoursChunk);
        content.add("。");
        document.add(content);

        Paragraph spacer = new Paragraph("\n\n");
        document.add(spacer);

        Paragraph thanks = new Paragraph("特此证明，以资鼓励。", contentFont);
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingBefore(20);
        thanks.setSpacingAfter(40);
        document.add(thanks);

        Paragraph date = new Paragraph(
                "证明开具日期：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                signatureFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingBefore(50);
        document.add(date);

        Paragraph signature = new Paragraph("VolunServe 志愿者服务平台", signatureFont);
        signature.setAlignment(Element.ALIGN_RIGHT);
        signature.setSpacingBefore(10);
        document.add(signature);

        document.close();

        byte[] pdfBytes = baos.toByteArray();

        String fileName = "certificate_" + serviceRecordId + "_" + System.currentTimeMillis() + ".pdf";
        Path uploadDir = Paths.get(uploadPath, "certificates");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path filePath = uploadDir.resolve(fileName);

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(pdfBytes);
        }

        serviceRecord.setCertificateUrl("/uploads/certificates/" + fileName);
        serviceRecord.setCertificateGeneratedAt(LocalDateTime.now());
        serviceRecordRepository.save(serviceRecord);

        return pdfBytes;
    }

    @Transactional
    public void markCertificateDownloaded(Long serviceRecordId) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(serviceRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("服务记录不存在"));
        serviceRecord.setCertificateDownloaded(true);
        serviceRecordRepository.save(serviceRecord);
    }

    private ServiceRecordDTO convertToDTO(ServiceRecord serviceRecord) {
        return ServiceRecordDTO.builder()
                .id(serviceRecord.getId())
                .registrationId(serviceRecord.getRegistration().getId())
                .volunteerId(serviceRecord.getVolunteer().getId())
                .volunteerName(serviceRecord.getVolunteer().getRealName())
                .volunteerAvatar(serviceRecord.getVolunteer().getAvatar())
                .activityId(serviceRecord.getActivity().getId())
                .activityTitle(serviceRecord.getActivity().getTitle())
                .serviceHours(serviceRecord.getServiceHours())
                .serviceDate(serviceRecord.getServiceDate())
                .volunteerFeedback(serviceRecord.getVolunteerFeedback())
                .volunteerRating(serviceRecord.getVolunteerRating())
                .organizerFeedback(serviceRecord.getOrganizerFeedback())
                .organizerRating(serviceRecord.getOrganizerRating())
                .certificateUrl(serviceRecord.getCertificateUrl())
                .certificateGeneratedAt(serviceRecord.getCertificateGeneratedAt())
                .certificateDownloaded(serviceRecord.getCertificateDownloaded())
                .createdAt(serviceRecord.getCreatedAt())
                .updatedAt(serviceRecord.getUpdatedAt())
                .build();
    }

    private PageResult<ServiceRecordDTO> convertToPageResult(Page<ServiceRecord> page) {
        return PageResult.<ServiceRecordDTO>builder()
                .content(page.getContent().stream().map(this::convertToDTO).collect(Collectors.toList()))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
