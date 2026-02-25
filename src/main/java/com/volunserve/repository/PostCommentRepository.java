package com.volunserve.repository;

import com.volunserve.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    Page<PostComment> findByPostIdAndParentIsNullAndEnabledTrue(Long postId, Pageable pageable);

    List<PostComment> findByPostIdAndParentIdAndEnabledTrue(Long postId, Long parentId);

    @Modifying
    @Query("UPDATE PostComment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.post.id = :postId AND c.enabled = true")
    long countByPostId(@Param("postId") Long postId);
}
