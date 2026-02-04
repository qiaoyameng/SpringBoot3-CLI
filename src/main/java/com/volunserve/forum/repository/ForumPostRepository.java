package com.volunserve.forum.repository;

import com.volunserve.forum.entity.ForumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long>, JpaSpecificationExecutor<ForumPost> {
    
    Page<ForumPost> findByActivityId(Long activityId, Pageable pageable);
    
    Page<ForumPost> findByUserId(Long userId, Pageable pageable);
    
    Page<ForumPost> findByIsExperienceTrue(Pageable pageable);
    
    Page<ForumPost> findByIsPhotoWallTrue(Pageable pageable);
    
    @Query("SELECT f FROM ForumPost f WHERE f.activityId = :activityId AND f.isExperience = true")
    Page<ForumPost> findExperiencePostsByActivityId(@Param("activityId") Long activityId, Pageable pageable);
    
    @Query("SELECT f FROM ForumPost f WHERE f.activityId = :activityId AND f.isPhotoWall = true")
    Page<ForumPost> findPhotoWallPostsByActivityId(@Param("activityId") Long activityId, Pageable pageable);
}
