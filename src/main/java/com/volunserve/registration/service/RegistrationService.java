package com.volunserve.registration.service;

import com.volunserve.activity.entity.Activity;
import com.volunserve.activity.enumeration.ActivityStatus;
import com.volunserve.activity.repository.ActivityRepository;
import com.volunserve.common.exception.BusinessException;
import com.volunserve.registration.dto.RegistrationApproveDTO;
import com.volunserve.registration.dto.RegistrationCreateDTO;
import com.volunserve.registration.dto.RegistrationDTO;
import com.volunserve.registration.dto.RegistrationQueryDTO;
import com.volunserve.registration.entity.Registration;
import com.volunserve.registration.enumeration.RegistrationStatus;
import com.volunserve.registration.repository.RegistrationRepository;
import com.volunserve.user.entity.User;
import com.volunserve.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Value("${application.notification.reminder-minutes-before:60}")
    private int reminderMinutesBefore;

    @Transactional
    public RegistrationDTO createRegistration(RegistrationCreateDTO dto) {
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new BusinessException("活动不存在"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (registrationRepository.existsByActivityAndUser(activity, user)) {
            throw new BusinessException("您已经报名此活动");
        }

        if (activity.getStatus() != ActivityStatus.RECRUITING) {
            throw new BusinessException("活动不在招募中");
        }

        int currentCount = registrationRepository.countApprovedByActivity(activity);
        if (currentCount >= activity.getRequiredVolunteers()) {
            throw new BusinessException("活动报名人数已满");
        }

        Registration registration = new Registration();
        registration.setActivity(activity);
        registration.setUser(user);
        registration.setApplyReason(dto.getApplyReason());

        if (activity.getAutoApprove()) {
            registration.setStatus(RegistrationStatus.APPROVED);
            registration.setApprovedAt(LocalDateTime.now());
            registration.setApprovedBy("system");
            activity.setCurrentVolunteers(currentCount + 1);
            activityRepository.save(activity);
            log.info("自动审核通过报名: activityId={}, userId={}", dto.getActivityId(), dto.getUserId());
        } else {
            registration.setStatus(RegistrationStatus.PENDING);
            log.info("提交报名申请: activityId={}, userId={}", dto.getActivityId(), dto.getUserId());
        }

        registration = registrationRepository.save(registration);
        return convertToDTO(registration);
    }

    @Transactional
    public RegistrationDTO approveRegistration(RegistrationApproveDTO dto) {
        Registration registration = registrationRepository.findById(dto.getRegistrationId())
                .orElseThrow(() -> new BusinessException("报名记录不存在"));

        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new BusinessException("只能审核待审核的报名");
        }

        if (dto.getApproved()) {
            Activity activity = registration.getActivity();
            int currentCount = registrationRepository.countApprovedByActivity(activity);
            if (currentCount >= activity.getRequiredVolunteers()) {
                throw new BusinessException("活动报名人数已满");
            }

            registration.setStatus(RegistrationStatus.APPROVED);
            registration.setApprovedAt(LocalDateTime.now());
            registration.setApprovedBy(dto.getApprovedBy());

            activity.setCurrentVolunteers(currentCount + 1);
            activityRepository.save(activity);
            log.info("审核通过报名: registrationId={}", dto.getRegistrationId());
        } else {
            registration.setStatus(RegistrationStatus.REJECTED);
            registration.setRejectReason(dto.getRejectReason());
            log.info("拒绝报名: registrationId={}, reason={}", dto.getRegistrationId(), dto.getRejectReason());
        }

        registration = registrationRepository.save(registration);
        return convertToDTO(registration);
    }

    @Transactional
    public RegistrationDTO checkIn(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("报名记录不存在"));

        if (registration.getStatus() != RegistrationStatus.APPROVED) {
            throw new BusinessException("只能对已通过审核的报名进行签到");
        }

        Activity activity = registration.getActivity();
        LocalDateTime now = LocalDateTime.now();

        if (activity.getCheckInStartTime() != null && now.isBefore(activity.getCheckInStartTime())) {
            throw new BusinessException("签到时间未到");
        }

        if (now.isAfter(activity.getStartTime())) {
            throw new BusinessException("活动已开始，无法签到");
        }

        registration.setStatus(RegistrationStatus.CHECKED_IN);
        registration.setCheckInTime(now);
        registration = registrationRepository.save(registration);

        log.info("签到成功: registrationId={}", registrationId);
        return convertToDTO(registration);
    }

    @Transactional
    public RegistrationDTO checkOut(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("报名记录不存在"));

        if (registration.getStatus() != RegistrationStatus.CHECKED_IN) {
            throw new BusinessException("只能对已签到的报名进行签退");
        }

        Activity activity = registration.getActivity();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(activity.getEndTime())) {
            throw new BusinessException("活动未结束，无法签退");
        }

        registration.setStatus(RegistrationStatus.CHECKED_OUT);
        registration.setCheckOutTime(now);
        registration = registrationRepository.save(registration);

        log.info("签退成功: registrationId={}", registrationId);
        return convertToDTO(registration);
    }

    @Transactional
    public void cancelRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("报名记录不存在"));

        if (registration.getStatus() == RegistrationStatus.CHECKED_IN ||
            registration.getStatus() == RegistrationStatus.CHECKED_OUT) {
            throw new BusinessException("已签到的报名无法取消");
        }

        if (registration.getStatus() == RegistrationStatus.APPROVED) {
            Activity activity = registration.getActivity();
            activity.setCurrentVolunteers(activity.getCurrentVolunteers() - 1);
            activityRepository.save(activity);
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        log.info("取消报名: registrationId={}", registrationId);
    }

    public RegistrationDTO getRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("报名记录不存在"));
        return convertToDTO(registration);
    }

    public Page<RegistrationDTO> getRegistrations(RegistrationQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(
                queryDTO.getPage(),
                queryDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<Registration> spec = buildSpecification(queryDTO);
        return registrationRepository.findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    public List<RegistrationDTO> getRegistrationsByActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException("活动不存在"));
        return registrationRepository.findByActivityAndStatusIn(activity,
                List.of(RegistrationStatus.APPROVED, RegistrationStatus.CHECKED_IN, RegistrationStatus.CHECKED_OUT))
                .stream().map(this::convertToDTO).toList();
    }

    @Transactional
    public void sendActivityReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(reminderMinutesBefore);

        List<Registration> registrations = registrationRepository.findUpcomingReminders(now, reminderTime);

        for (Registration registration : registrations) {
            log.info("发送活动提醒: registrationId={}, activity={}, user={}",
                    registration.getId(),
                    registration.getActivity().getTitle(),
                    registration.getUser().getUsername());

            registration.setReminded(true);
            registrationRepository.save(registration);
        }
    }

    private RegistrationDTO convertToDTO(Registration registration) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(registration.getId());
        dto.setActivityId(registration.getActivity().getId());
        dto.setActivityTitle(registration.getActivity().getTitle());
        dto.setUserId(registration.getUser().getId());
        dto.setUserName(registration.getUser().getRealName());
        dto.setStatus(registration.getStatus());
        dto.setApplyReason(registration.getApplyReason());
        dto.setApprovedAt(registration.getApprovedAt());
        dto.setApprovedBy(registration.getApprovedBy());
        dto.setRejectReason(registration.getRejectReason());
        dto.setCheckInTime(registration.getCheckInTime());
        dto.setCheckOutTime(registration.getCheckOutTime());
        dto.setCreatedAt(registration.getCreatedAt());
        dto.setUpdatedAt(registration.getUpdatedAt());
        return dto;
    }

    private Specification<Registration> buildSpecification(RegistrationQueryDTO queryDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDTO.getActivityId() != null) {
                predicates.add(cb.equal(root.get("activity").get("id"), queryDTO.getActivityId()));
            }

            if (queryDTO.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), queryDTO.getUserId()));
            }

            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
