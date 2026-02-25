package com.volunserve.repository;

import com.volunserve.entity.ForumPost;
import com.volunserve.entity.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    Page<ForumPost> findByTypeAndEnabledTrue(PostType type, Pageable pageable);

    Page<ForumPost> findByActivityIdAndEnabledTrue(Long activityId, Pageable pageable);

    Page<ForumPost> findByAuthorIdAndEnabledTrue(Long authorId, Pageable pageable);

    @Query("SELECT p FROM ForumPost p WHERE p.enabled = true AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:activityId IS NULL OR p.activity.id = :activityId) AND " +
           "(:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<ForumPost> searchPosts(@Param("type") PostType type, 
                                @Param("activityId") Long activityId,
                                @Param("keyword") String keyword, 
                                Pageable pageable);

    @Query("SELECT p FROM ForumPost p WHERE p.enabled = true AND p.isTop = true ORDER BY p.createdAt DESC")
    List<ForumPost> findTopPosts();

    @Query("SELECT p FROM ForumPost p WHERE p.enabled = true AND p.isEssence = true ORDER BY p.createdAt DESC")
    List<ForumPost> findEssencePosts();

    @Modifying
    @Query("UPDATE ForumPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ForumPost p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ForumPost p SET p.commentCount = p.commentCount + 1 WHERE p.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ForumPost p SET p.commentCount = p.commentCount - 1 WHERE p.id = :id")
    void decrementCommentCount(@Param("id") Long id);
}
