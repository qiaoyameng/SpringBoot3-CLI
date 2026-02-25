package com.volunserve.service;

import com.volunserve.dto.*;
import com.volunserve.entity.Activity;
import com.volunserve.entity.Registration;
import com.volunserve.entity.User;
import com.volunserve.entity.enums.ActivityStatus;
import com.volunserve.entity.enums.AuditType;
import com.volunserve.entity.enums.CheckInStatus;
import com.volunserve.entity.enums.RegistrationStatus;
import com.volunserve.exception.BusinessException;
import com.volunserve.exception.ResourceNotFoundException;
import com.volunserve.repository.ActivityRepository;
import com.volunserve.repository.RegistrationRepository;
import com.volunserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public RegistrationDTO register(RegistrationCreateDTO dto, Long volunteerId) {
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("活动不存在"));

        if (activity.getStatus() != ActivityStatus.RECRUITING) {
            throw new BusinessException("活动不在招募中，无法报名");
        }

        if (activity.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("活动已开始，无法报名");
        }

        if (registrationRepository.existsByActivityIdAndVolunteerId(activity.getId(), volunteerId)) {
            throw new BusinessException("您已经报名过该活动");
        }

        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        RegistrationStatus status = activity.getAuditType() == AuditType.AUTO 
                ? RegistrationStatus.APPROVED : RegistrationStatus.PENDING;

        Registration registration = Registration.builder()
                .activity(activity)
                .volunteer(volunteer)
                .status(status)
                .checkInStatus(CheckInStatus.NOT_CHECKED_IN)
                .message(dto.getMessage())
                .reminderSent(false)
                .build();

        Registration saved = registrationRepository.save(registration);

        activityRepository.incrementRegisteredCount(activity.getId());

        if (status == RegistrationStatus.APPROVED) {
            activityRepository.incrementApprovedCount(activity.getId());
            notificationService.sendRegistrationApprovedNotification(saved);
        } else {
            notificationService.sendRegistrationPendingNotification(saved);
        }

        return convertToDTO(saved);
    }

    @Transactional
    public RegistrationDTO reviewRegistration(Long registrationId, RegistrationReviewDTO dto, Long reviewerId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("报名记录不存在"));

        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new BusinessException("该报名记录已审核");
        }

        registration.setStatus(dto.getStatus());
        registration.setAdminComment(dto.getAdminComment());
        registration.setReviewedBy(userRepository.findById(reviewerId).orElse(null));
        registration.setReviewedAt(LocalDateTime.now());

        Registration saved = registrationRepository.save(registration);

        if (dto.getStatus() == RegistrationStatus.APPROVED) {
            activityRepository.incrementApprovedCount(registration.getActivity().getId());
            notificationService.sendRegistrationApprovedNotification(saved);
        } else if (dto.getStatus() == RegistrationStatus.REJECTED) {
            notificationService.sendRegistrationRejectedNotification(saved);
        }

        return convertToDTO(saved);
    }

    @Transactional
    public void cancelRegistration(Long registrationId, Long volunteerId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("报名记录不存在"));

        if (!registration.getVolunteer().getId().equals(volunteerId)) {
            throw new BusinessException("无权取消该报名");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new BusinessException("报名已取消");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

        activityRepository.decrementRegisteredCount(registration.getActivity().getId());
        if (registration.getStatus() == RegistrationStatus.APPROVED) {
            activityRepository.decrementApprovedCount(registration.getActivity().getId());
        }
    }

    @Transactional
    public RegistrationDTO checkIn(CheckInDTO dto) {
        Registration registration = registrationRepository.findById(dto.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("报名记录不存在"));

        if (registration.getStatus() != RegistrationStatus.APPROVED) {
            throw new BusinessException("报名未通过审核，无法签到");
        }

        if (registration.getCheckInStatus() != CheckInStatus.NOT_CHECKED_IN) {
            throw new BusinessException("已签到，无需重复签到");
        }

        Activity activity = registration.getActivity();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(activity.getStartTime().minusMinutes(30))) {
            throw new BusinessException("签到时间未到，请在活动开始前30分钟内签到");
        }

        registration.setCheckInStatus(CheckInStatus.CHECKED_IN);
        registration.setCheckInTime(now);

        Registration saved = registrationRepository.save(registration);
        return convertToDTO(saved);
    }

    @Transactional
    public RegistrationDTO checkOut(CheckInDTO dto) {
        Registration registration = registrationRepository.findById(dto.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("报名记录不存在"));

        if (registration.getCheckInStatus() != CheckInStatus.CHECKED_IN) {
            throw new BusinessException("尚未签到，无法签出");
        }

        registration.setCheckInStatus(CheckInStatus.CHECKED_OUT);
        registration.setCheckOutTime(LocalDateTime.now());

        int hours = (int) ChronoUnit.HOURS.between(registration.getCheckInTime(), registration.getCheckOutTime());
        registration.setServiceHours(Math.max(hours, 1));

        Registration saved = registrationRepository.save(registration);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public RegistrationDTO getRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("报名记录不存在"));
        return convertToDTO(registration);
    }

    @Transactional(readOnly = true)
    public PageResult<RegistrationDTO> getMyRegistrations(Long volunteerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Registration> registrationPage = registrationRepository.findByVolunteerId(volunteerId, pageable);
        return convertToPageResult(registrationPage);
    }

    @Transactional(readOnly = true)
    public PageResult<RegistrationDTO> getActivityRegistrations(Long activityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Registration> registrationPage = registrationRepository.findByActivityId(activityId, pageable);
        return convertToPageResult(registrationPage);
    }

    @Transactional(readOnly = true)
    public List<RegistrationDTO> getActivityRegistrationsByStatus(Long activityId, RegistrationStatus status) {
        List<Registration> registrations = registrationRepository.findByActivityIdAndStatus(activityId, status);
        return registrations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistrationDTO getRegistrationByActivityAndVolunteer(Long activityId, Long volunteerId) {
        Registration registration = registrationRepository.findByActivityIdAndVolunteerId(activityId, volunteerId)
                .orElse(null);
        return registration != null ? convertToDTO(registration) : null;
    }

    private RegistrationDTO convertToDTO(Registration registration) {
        RegistrationDTO dto = RegistrationDTO.builder()
                .id(registration.getId())
                .activityId(registration.getActivity().getId())
                .activityTitle(registration.getActivity().getTitle())
                .volunteerId(registration.getVolunteer().getId())
                .volunteerName(registration.getVolunteer().getRealName())
                .volunteerAvatar(registration.getVolunteer().getAvatar())
                .status(registration.getStatus())
                .statusLabel(registration.getStatus().getLabel())
                .checkInStatus(registration.getCheckInStatus())
                .checkInStatusLabel(registration.getCheckInStatus().getLabel())
                .message(registration.getMessage())
                .adminComment(registration.getAdminComment())
                .checkInTime(registration.getCheckInTime())
                .checkOutTime(registration.getCheckOutTime())
                .serviceHours(registration.getServiceHours())
                .reminderSent(registration.getReminderSent())
                .createdAt(registration.getCreatedAt())
                .updatedAt(registration.getUpdatedAt())
                .build();

        if (registration.getReviewedBy() != null) {
            dto.setReviewedBy(registration.getReviewedBy().getId());
            dto.setReviewedByName(registration.getReviewedBy().getRealName());
        }
        dto.setReviewedAt(registration.getReviewedAt());

        return dto;
    }

    private PageResult<RegistrationDTO> convertToPageResult(Page<Registration> page) {
        return PageResult.<RegistrationDTO>builder()
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
