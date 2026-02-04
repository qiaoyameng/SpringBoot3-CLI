package com.volunserve.forum.repository;

import com.volunserve.forum.entity.ForumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumPhotoRepository extends JpaRepository<ForumPhoto, Long> {
    
    List<ForumPhoto> findByPostId(Long postId);
    
    void deleteByPostId(Long postId);
}
