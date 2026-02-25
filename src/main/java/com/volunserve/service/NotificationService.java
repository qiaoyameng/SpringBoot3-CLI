package com.volunserve.service;

import com.volunserve.dto.NotificationDTO;
import com.volunserve.dto.PageResult;
import com.volunserve.entity.Activity;
import com.volunserve.entity.Notification;
import com.volunserve.entity.Registration;
import com.volunserve.entity.ServiceRecord;
import com.volunserve.entity.User;
import com.volunserve.exception.ResourceNotFoundException;
import com.volunserve.repository.NotificationRepository;
import com.volunserve.repository.RegistrationRepository;
import com.volunserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final JavaMailSender mailSender;

    @Transactional(readOnly = true)
    public PageResult<NotificationDTO> getMyNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return convertToPageResult(notificationPage);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作该通知");
        }

        notificationRepository.markAsRead(notificationId, LocalDateTime.now());
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    @Transactional
    public Notification createNotification(Long userId, String title, String content, String type, 
                                           String relatedEntityType, Long relatedEntityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .type(type)
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Async
    public void sendRegistrationApprovedNotification(Registration registration) {
        try {
            Activity activity = registration.getActivity();
            User volunteer = registration.getVolunteer();

            String title = "报名审核通过";
            String content = String.format("恭喜！您报名的活动《%s》已通过审核。活动时间：%s，地点：%s",
                    activity.getTitle(),
                    activity.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    activity.getLocation());

            createNotification(volunteer.getId(), title, content, "REGISTRATION", "Activity", activity.getId());

            if (volunteer.getEmail() != null) {
                sendEmail(volunteer.getEmail(), title, content);
            }
        } catch (Exception e) {
            log.error("发送报名通过通知失败", e);
        }
    }

    @Async
    public void sendRegistrationRejectedNotification(Registration registration) {
        try {
            Activity activity = registration.getActivity();
            User volunteer = registration.getVolunteer();

            String title = "报名审核未通过";
            String content = String.format("抱歉，您报名的活动《%s》未通过审核。原因：%s",
                    activity.getTitle(),
                    registration.getAdminComment() != null ? registration.getAdminComment() : "无");

            createNotification(volunteer.getId(), title, content, "REGISTRATION", "Activity", activity.getId());

            if (volunteer.getEmail() != null) {
                sendEmail(volunteer.getEmail(), title, content);
            }
        } catch (Exception e) {
            log.error("发送报名拒绝通知失败", e);
        }
    }

    @Async
    public void sendRegistrationPendingNotification(Registration registration) {
        try {
            Activity activity = registration.getActivity();
            User volunteer = registration.getVolunteer();

            String title = "报名提交成功";
            String content = String.format("您已成功提交活动《%s》的报名申请，请等待审核。",
                    activity.getTitle());

            createNotification(volunteer.getId(), title, content, "REGISTRATION", "Activity", activity.getId());
        } catch (Exception e) {
            log.error("发送报名提交通知失败", e);
        }
    }

    @Async
    public void sendActivityReminder(Registration registration) {
        try {
            Activity activity = registration.getActivity();
            User volunteer = registration.getVolunteer();

            String title = "活动即将开始";
            String content = String.format("您报名的活动《%s》将在1小时后开始，请准时参加。地点：%s",
                    activity.getTitle(),
                    activity.getLocation());

            createNotification(volunteer.getId(), title, content, "REMINDER", "Activity", activity.getId());

            if (volunteer.getEmail() != null) {
                sendEmail(volunteer.getEmail(), title, content);
            }

            registrationRepository.markReminderSent(registration.getId());
        } catch (Exception e) {
            log.error("发送活动提醒失败", e);
        }
    }

    @Async
    public void sendServiceRecordCreatedNotification(ServiceRecord serviceRecord) {
        try {
            Activity activity = serviceRecord.getActivity();
            User volunteer = serviceRecord.getVolunteer();

            String title = "服务记录已生成";
            String content = String.format("您参加的活动《%s》服务记录已生成，服务时长：%d小时。",
                    activity.getTitle(),
                    serviceRecord.getServiceHours());

            createNotification(volunteer.getId(), title, content, "SERVICE_RECORD", "ServiceRecord", serviceRecord.getId());

            if (volunteer.getEmail() != null) {
                sendEmail(volunteer.getEmail(), title, content);
            }
        } catch (Exception e) {
            log.error("发送服务记录通知失败", e);
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendActivityReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourLater = now.plusHours(1);

            List<Registration> pendingReminders = registrationRepository.findPendingReminders(now, oneHourLater);

            for (Registration registration : pendingReminders) {
                sendActivityReminder(registration);
            }

            if (!pendingReminders.isEmpty()) {
                log.info("已发送 {} 条活动提醒", pendingReminders.size());
            }
        } catch (Exception e) {
            log.error("定时发送活动提醒失败", e);
        }
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("[VolunServe] " + subject);
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送邮件失败: {}", to, e);
        }
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .relatedEntityType(notification.getRelatedEntityType())
                .relatedEntityId(notification.getRelatedEntityId())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private PageResult<NotificationDTO> convertToPageResult(Page<Notification> page) {
        return PageResult.<NotificationDTO>builder()
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
