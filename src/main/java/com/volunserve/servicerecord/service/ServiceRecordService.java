package com.volunserve.servicerecord.service;

import com.volunserve.activity.entity.Activity;
import com.volunserve.activity.repository.ActivityRepository;
import com.volunserve.common.exception.BusinessException;
import com.volunserve.registration.entity.Registration;
import com.volunserve.registration.enumeration.RegistrationStatus;
import com.volunserve.registration.repository.RegistrationRepository;
import com.volunserve.servicerecord.dto.ServiceRecordDTO;
import com.volunserve.servicerecord.dto.ServiceRecordQueryDTO;
import com.volunserve.servicerecord.dto.ServiceReviewDTO;
import com.volunserve.servicerecord.entity.ServiceRecord;
import com.volunserve.servicerecord.repository.ServiceRecordRepository;
import com.volunserve.user.entity.User;
import com.volunserve.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final RegistrationRepository registrationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public ServiceRecordDTO createServiceRecordFromRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("报名记录不存在"));

        if (registration.getStatus() != RegistrationStatus.CHECKED_OUT) {
            throw new BusinessException("只有签退后才能创建服务记录");
        }

        if (registration.getCheckInTime() == null || registration.getCheckOutTime() == null) {
            throw new BusinessException("签到或签退时间缺失");
        }

        Activity activity = registration.getActivity();
        User user = registration.getUser();

        if (serviceRecordRepository.findByActivityAndUser(activity, user).isPresent()) {
            throw new BusinessException("服务记录已存在");
        }

        Duration duration = Duration.between(registration.getCheckInTime(), registration.getCheckOutTime());
        BigDecimal serviceHours = BigDecimal.valueOf(duration.toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        ServiceRecord serviceRecord = new ServiceRecord();
        serviceRecord.setActivity(activity);
        serviceRecord.setUser(user);
        serviceRecord.setCheckInTime(registration.getCheckInTime());
        serviceRecord.setCheckOutTime(registration.getCheckOutTime());
        serviceRecord.setServiceHours(serviceHours);
        serviceRecord.setCertificateGenerated(false);

        serviceRecord = serviceRecordRepository.save(serviceRecord);

        user.setTotalServiceHours(user.getTotalServiceHours() + serviceHours.intValue());
        user.setActivityCount(user.getActivityCount() + 1);
        userRepository.save(user);

        log.info("创建服务记录: registrationId={}, serviceHours={}", registrationId, serviceHours);
        return convertToDTO(serviceRecord);
    }

    @Transactional
    public ServiceRecordDTO submitReview(ServiceReviewDTO dto) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(dto.getServiceRecordId())
                .orElseThrow(() -> new BusinessException("服务记录不存在"));

        if (serviceRecord.getRating() != null) {
            throw new BusinessException("已评价，无法重复评价");
        }

        serviceRecord.setRating(dto.getRating());
        serviceRecord.setReviewContent(dto.getReviewContent());
        serviceRecord = serviceRecordRepository.save(serviceRecord);

        log.info("提交服务评价: serviceRecordId={}, rating={}", dto.getServiceRecordId(), dto.getRating());
        return convertToDTO(serviceRecord);
    }

    @Transactional
    public ServiceRecordDTO generateCertificate(Long serviceRecordId) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(serviceRecordId)
                .orElseThrow(() -> new BusinessException("服务记录不存在"));

        if (serviceRecord.getCertificateGenerated()) {
            throw new BusinessException("服务证明已生成");
        }

        User user = serviceRecord.getUser();
        Activity activity = serviceRecord.getActivity();

        String certificateNumber = generateCertificateNumber(user.getId(), activity.getId());

        serviceRecord.setCertificateNumber(certificateNumber);
        serviceRecord.setCertificateGeneratedAt(LocalDateTime.now());
        serviceRecord.setCertificateGenerated(true);
        serviceRecord = serviceRecordRepository.save(serviceRecord);

        log.info("生成服务证明: serviceRecordId={}, certificateNumber={}", serviceRecordId, certificateNumber);
        return convertToDTO(serviceRecord);
    }

    public String getCertificateContent(Long serviceRecordId) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(serviceRecordId)
                .orElseThrow(() -> new BusinessException("服务记录不存在"));

        if (!serviceRecord.getCertificateGenerated()) {
            throw new BusinessException("服务证明未生成");
        }

        User user = serviceRecord.getUser();
        Activity activity = serviceRecord.getActivity();

        return String.format("""
            志愿服务证明

            证书编号: %s

            兹证明 %s 同志于 %s 至 %s 期间，
            参加了由本平台组织的 "%s" 志愿服务活动。

            服务地点: %s
            服务时长: %.2f 小时
            服务评价: %s 星

            感谢您的无私奉献！

            社区志愿者服务平台
            颁发日期: %s
            """,
                serviceRecord.getCertificateNumber(),
                user.getRealName(),
                serviceRecord.getCheckInTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")),
                serviceRecord.getCheckOutTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")),
                activity.getTitle(),
                activity.getLocation(),
                serviceRecord.getServiceHours(),
                serviceRecord.getRating() != null ? serviceRecord.getRating() + "" : "未评价",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
        );
    }

    public ServiceRecordDTO getServiceRecord(Long id) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务记录不存在"));
        return convertToDTO(serviceRecord);
    }

    public Page<ServiceRecordDTO> getServiceRecords(ServiceRecordQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(
                queryDTO.getPage(),
                queryDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<ServiceRecord> spec = buildSpecification(queryDTO);
        return serviceRecordRepository.findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    public List<ServiceRecordDTO> getServiceRecordsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return serviceRecordRepository.findByUser(user)
                .stream().map(this::convertToDTO).toList();
    }

    public BigDecimal getTotalServiceHoursByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        Double totalHours = serviceRecordRepository.sumServiceHoursByUser(user);
        return BigDecimal.valueOf(totalHours != null ? totalHours : 0.0);
    }

    private String generateCertificateNumber(Long userId, Long activityId) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("VS-%s-%d-%d-%s", dateStr, userId, activityId, uuid);
    }

    private ServiceRecordDTO convertToDTO(ServiceRecord serviceRecord) {
        ServiceRecordDTO dto = new ServiceRecordDTO();
        dto.setId(serviceRecord.getId());
        dto.setActivityId(serviceRecord.getActivity().getId());
        dto.setActivityTitle(serviceRecord.getActivity().getTitle());
        dto.setUserId(serviceRecord.getUser().getId());
        dto.setUserName(serviceRecord.getUser().getRealName());
        dto.setCheckInTime(serviceRecord.getCheckInTime());
        dto.setCheckOutTime(serviceRecord.getCheckOutTime());
        dto.setServiceHours(serviceRecord.getServiceHours());
        dto.setRating(serviceRecord.getRating());
        dto.setReviewContent(serviceRecord.getReviewContent());
        dto.setFeedback(serviceRecord.getFeedback());
        dto.setCertificateNumber(serviceRecord.getCertificateNumber());
        dto.setCertificateGeneratedAt(serviceRecord.getCertificateGeneratedAt());
        dto.setCertificateGenerated(serviceRecord.getCertificateGenerated());
        dto.setCreatedAt(serviceRecord.getCreatedAt());
        dto.setUpdatedAt(serviceRecord.getUpdatedAt());
        return dto;
    }

    private Specification<ServiceRecord> buildSpecification(ServiceRecordQueryDTO queryDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDTO.getActivityId() != null) {
                predicates.add(cb.equal(root.get("activity").get("id"), queryDTO.getActivityId()));
            }

            if (queryDTO.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), queryDTO.getUserId()));
            }

            if (queryDTO.getCertificateGenerated() != null) {
                predicates.add(cb.equal(root.get("certificateGenerated"), queryDTO.getCertificateGenerated()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
