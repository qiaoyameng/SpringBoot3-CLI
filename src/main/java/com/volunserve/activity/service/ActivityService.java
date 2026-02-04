package com.volunserve.activity.service;

import com.volunserve.activity.dto.ActivityCreateDTO;
import com.volunserve.activity.dto.ActivityDTO;
import com.volunserve.activity.dto.ActivityQueryDTO;
import com.volunserve.activity.dto.ActivityUpdateDTO;
import com.volunserve.activity.entity.Activity;
import com.volunserve.activity.enumeration.ActivityStatus;
import com.volunserve.activity.mapper.ActivityMapper;
import com.volunserve.activity.repository.ActivityRepository;
import com.volunserve.common.exception.BusinessException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    @Transactional
    public ActivityDTO createActivity(ActivityCreateDTO dto) {
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }

        Activity activity = activityMapper.toEntity(dto);
        activity.setStatus(ActivityStatus.RECRUITING);
        activity.setCurrentVolunteers(0);

        activity = activityRepository.save(activity);
        log.info("创建活动成功: id={}, title={}", activity.getId(), activity.getTitle());
        return activityMapper.toDTO(activity);
    }

    @Transactional
    public ActivityDTO updateActivity(Long id, ActivityUpdateDTO dto) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }

        if (activity.getStatus() == ActivityStatus.COMPLETED) {
            throw new BusinessException("已完成的活动不能修改");
        }

        activityMapper.updateEntity(dto, activity);
        activity = activityRepository.save(activity);
        log.info("更新活动成功: id={}", id);
        return activityMapper.toDTO(activity);
    }

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        if (activity.getStatus() == ActivityStatus.ONGOING) {
            throw new BusinessException("进行中的活动不能删除");
        }

        activityRepository.delete(activity);
        log.info("删除活动成功: id={}", id);
    }

    public ActivityDTO getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));
        return activityMapper.toDTO(activity);
    }

    public Page<ActivityDTO> getActivities(ActivityQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(
                queryDTO.getPage(),
                queryDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<Activity> spec = buildSpecification(queryDTO);
        return activityRepository.findAll(spec, pageable)
                .map(activityMapper::toDTO);
    }

    public List<ActivityDTO> getUpcomingActivities() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);
        List<Activity> activities = activityRepository.findUpcomingActivities(now, oneDayLater);
        return activities.stream().map(activityMapper::toDTO).toList();
    }

    @Transactional
    public void updateActivityStatus(Long id, ActivityStatus status) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));
        activity.setStatus(status);
        activityRepository.save(activity);
        log.info("更新活动状态: id={}, status={}", id, status);
    }

    @Transactional
    public void updateExpiredActivities() {
        LocalDateTime now = LocalDateTime.now();

        List<Activity> recruitingActivities = activityRepository
                .findByStatusAndStartTimeBefore(ActivityStatus.RECRUITING, now);
        for (Activity activity : recruitingActivities) {
            activity.setStatus(ActivityStatus.ONGOING);
            activityRepository.save(activity);
            log.info("活动自动转为进行中: id={}", activity.getId());
        }

        List<Activity> ongoingActivities = activityRepository
                .findByStatusAndEndTimeBefore(ActivityStatus.ONGOING, now);
        for (Activity activity : ongoingActivities) {
            activity.setStatus(ActivityStatus.COMPLETED);
            activityRepository.save(activity);
            log.info("活动自动转为已完成: id={}", activity.getId());
        }
    }

    private Specification<Activity> buildSpecification(ActivityQueryDTO queryDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
                String keyword = "%" + queryDTO.getKeyword() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), keyword),
                        cb.like(root.get("description"), keyword),
                        cb.like(root.get("location"), keyword)
                ));
            }

            if (queryDTO.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), queryDTO.getCategory()));
            }

            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }

            if (queryDTO.getStartTimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), queryDTO.getStartTimeFrom()));
            }

            if (queryDTO.getStartTimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), queryDTO.getStartTimeTo()));
            }

            if (queryDTO.getLocation() != null && !queryDTO.getLocation().isEmpty()) {
                predicates.add(cb.like(root.get("location"), "%" + queryDTO.getLocation() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
