package com.volunserve.service;

import com.volunserve.dto.*;
import com.volunserve.entity.Activity;
import com.volunserve.entity.User;
import com.volunserve.entity.enums.ActivityCategory;
import com.volunserve.entity.enums.ActivityStatus;
import com.volunserve.exception.BusinessException;
import com.volunserve.exception.ResourceNotFoundException;
import com.volunserve.repository.ActivityRepository;
import com.volunserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public ActivityDTO createActivity(ActivityCreateDTO dto, Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("组织者不存在"));

        Activity activity = Activity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .status(ActivityStatus.RECRUITING)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(dto.getLocation())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .requiredVolunteers(dto.getRequiredVolunteers())
                .registeredCount(0)
                .approvedCount(0)
                .auditType(dto.getAuditType())
                .requirements(dto.getRequirements())
                .contactInfo(dto.getContactInfo())
                .organizer(organizer)
                .publishedAt(LocalDateTime.now())
                .build();

        Activity saved = activityRepository.save(activity);
        return convertToDTO(saved);
    }

    @Transactional
    public ActivityDTO updateActivity(Long id, ActivityUpdateDTO dto) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("活动不存在"));

        if (dto.getTitle() != null) activity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) activity.setDescription(dto.getDescription());
        if (dto.getCategory() != null) activity.setCategory(dto.getCategory());
        if (dto.getStartTime() != null) activity.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) activity.setEndTime(dto.getEndTime());
        if (dto.getLocation() != null) activity.setLocation(dto.getLocation());
        if (dto.getLatitude() != null) activity.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) activity.setLongitude(dto.getLongitude());
        if (dto.getRequiredVolunteers() != null) activity.setRequiredVolunteers(dto.getRequiredVolunteers());
        if (dto.getStatus() != null) activity.setStatus(dto.getStatus());
        if (dto.getAuditType() != null) activity.setAuditType(dto.getAuditType());
        if (dto.getRequirements() != null) activity.setRequirements(dto.getRequirements());
        if (dto.getContactInfo() != null) activity.setContactInfo(dto.getContactInfo());

        Activity saved = activityRepository.save(activity);
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("活动不存在"));
        activityRepository.delete(activity);
    }

    @Transactional(readOnly = true)
    public ActivityDTO getActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("活动不存在"));
        return convertToDTO(activity);
    }

    @Transactional(readOnly = true)
    public PageResult<ActivityDTO> listActivities(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Activity> activityPage = activityRepository.findAll(pageable);
        return convertToPageResult(activityPage);
    }

    @Transactional(readOnly = true)
    public PageResult<ActivityDTO> searchActivities(ActivityQueryDTO query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Activity> activityPage = activityRepository.searchActivities(
                query.getKeyword(),
                query.getCategory(),
                query.getStatus(),
                query.getStartTimeFrom(),
                query.getStartTimeTo(),
                query.getOrganizerId(),
                pageable
        );
        return convertToPageResult(activityPage);
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getNearbyActivities(Double latitude, Double longitude, Double radiusKm) {
        List<Activity> activities = activityRepository.findNearbyActivities(latitude, longitude, radiusKm);
        return activities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResult<ActivityDTO> getActivitiesByCategory(ActivityCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Activity> activityPage = activityRepository.findByCategory(category, pageable);
        return convertToPageResult(activityPage);
    }

    @Transactional(readOnly = true)
    public PageResult<ActivityDTO> getActivitiesByOrganizer(Long organizerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Activity> activityPage = activityRepository.findByOrganizerId(organizerId, pageable);
        return convertToPageResult(activityPage);
    }

    @Transactional
    public void updateActivityStatus(Long id, ActivityStatus status) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("活动不存在"));
        activity.setStatus(status);
        activityRepository.save(activity);
    }

    private ActivityDTO convertToDTO(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .category(activity.getCategory())
                .categoryLabel(activity.getCategory().getLabel())
                .status(activity.getStatus())
                .statusLabel(activity.getStatus().getLabel())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .location(activity.getLocation())
                .latitude(activity.getLatitude())
                .longitude(activity.getLongitude())
                .requiredVolunteers(activity.getRequiredVolunteers())
                .registeredCount(activity.getRegisteredCount())
                .approvedCount(activity.getApprovedCount())
                .auditType(activity.getAuditType())
                .auditTypeLabel(activity.getAuditType().getLabel())
                .requirements(activity.getRequirements())
                .contactInfo(activity.getContactInfo())
                .organizerId(activity.getOrganizer().getId())
                .organizerName(activity.getOrganizer().getRealName())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .publishedAt(activity.getPublishedAt())
                .build();
    }

    private PageResult<ActivityDTO> convertToPageResult(Page<Activity> page) {
        return PageResult.<ActivityDTO>builder()
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
