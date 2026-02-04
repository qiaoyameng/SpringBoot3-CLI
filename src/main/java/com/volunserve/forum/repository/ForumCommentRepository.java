package com.volunserve.forum.repository;

import com.volunserve.forum.entity.ForumComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    
    Page<ForumComment> findByPostId(Long postId, Pageable pageable);
    
    void deleteByPostId(Long postId);
}
