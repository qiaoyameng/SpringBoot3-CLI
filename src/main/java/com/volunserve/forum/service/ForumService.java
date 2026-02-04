package com.volunserve.forum.service;

import com.volunserve.common.exception.BusinessException;
import com.volunserve.forum.dto.*;
import com.volunserve.forum.entity.ForumComment;
import com.volunserve.forum.entity.ForumPhoto;
import com.volunserve.forum.entity.ForumPost;
import com.volunserve.forum.repository.ForumCommentRepository;
import com.volunserve.forum.repository.ForumPhotoRepository;
import com.volunserve.forum.repository.ForumPostRepository;
import com.volunserve.user.entity.User;
import com.volunserve.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumPostRepository forumPostRepository;
    private final ForumPhotoRepository forumPhotoRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ForumPostDTO createPost(ForumPostCreateDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        ForumPost post = new ForumPost();
        post.setActivityId(dto.getActivityId());
        post.setUserId(dto.getUserId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setIsExperience(dto.getIsExperience());
        post.setIsPhotoWall(dto.getIsPhotoWall());
        post.setLikeCount(0);
        post.setViewCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        ForumPost savedPost = forumPostRepository.save(post);

        if (dto.getPhotoUrls() != null && !dto.getPhotoUrls().isEmpty()) {
            for (String photoUrl : dto.getPhotoUrls()) {
                ForumPhoto photo = new ForumPhoto();
                photo.setPostId(savedPost.getId());
                photo.setPhotoUrl(photoUrl);
                photo.setCreatedAt(LocalDateTime.now());
                forumPhotoRepository.save(photo);
            }
        }

        return convertToDTO(savedPost, user);
    }

    @Transactional
    public ForumPostDTO updatePost(Long postId, Long userId, ForumPostUpdateDTO dto) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此帖子");
        }

        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }
        post.setUpdatedAt(LocalDateTime.now());

        ForumPost savedPost = forumPostRepository.save(post);

        if (dto.getPhotoUrls() != null) {
            forumPhotoRepository.deleteByPostId(postId);
            for (String photoUrl : dto.getPhotoUrls()) {
                ForumPhoto photo = new ForumPhoto();
                photo.setPostId(postId);
                photo.setPhotoUrl(photoUrl);
                photo.setCreatedAt(LocalDateTime.now());
                forumPhotoRepository.save(photo);
            }
        }

        User user = userRepository.findById(userId).orElse(null);
        return convertToDTO(savedPost, user);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此帖子");
        }

        forumCommentRepository.deleteByPostId(postId);
        forumPhotoRepository.deleteByPostId(postId);
        forumPostRepository.delete(post);
    }

    public ForumPostDTO getPostById(Long postId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        post.setViewCount(post.getViewCount() + 1);
        forumPostRepository.save(post);

        User user = userRepository.findById(post.getUserId()).orElse(null);
        return convertToDTO(post, user);
    }

    public Page<ForumPostDTO> getAllPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return forumPostRepository.findAll(pageRequest)
                .map(post -> {
                    User user = userRepository.findById(post.getUserId()).orElse(null);
                    return convertToDTO(post, user);
                });
    }

    public Page<ForumPostDTO> getExperiencePosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return forumPostRepository.findByIsExperienceTrue(pageRequest)
                .map(post -> {
                    User user = userRepository.findById(post.getUserId()).orElse(null);
                    return convertToDTO(post, user);
                });
    }

    public Page<ForumPostDTO> getPhotoWallPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return forumPostRepository.findByIsPhotoWallTrue(pageRequest)
                .map(post -> {
                    User user = userRepository.findById(post.getUserId()).orElse(null);
                    return convertToDTO(post, user);
                });
    }

    public Page<ForumPostDTO> getPostsByActivity(Long activityId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return forumPostRepository.findByActivityId(activityId, pageRequest)
                .map(post -> {
                    User user = userRepository.findById(post.getUserId()).orElse(null);
                    return convertToDTO(post, user);
                });
    }

    public Page<ForumPostDTO> getPostsByUser(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return forumPostRepository.findByUserId(userId, pageRequest)
                .map(post -> {
                    User user = userRepository.findById(post.getUserId()).orElse(null);
                    return convertToDTO(post, user);
                });
    }

    @Transactional
    public void likePost(Long postId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        post.setLikeCount(post.getLikeCount() + 1);
        forumPostRepository.save(post);
    }

    @Transactional
    public ForumCommentDTO addComment(Long postId, Long userId, ForumCommentCreateDTO dto) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        ForumComment comment = new ForumComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());

        ForumComment savedComment = forumCommentRepository.save(comment);
        return convertCommentToDTO(savedComment, user);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        ForumComment comment = forumCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("评论不存在"));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此评论");
        }

        forumCommentRepository.delete(comment);
    }

    public Page<ForumCommentDTO> getCommentsByPost(Long postId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return forumCommentRepository.findByPostId(postId, pageRequest)
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElse(null);
                    return convertCommentToDTO(comment, user);
                });
    }

    private ForumPostDTO convertToDTO(ForumPost post, User user) {
        ForumPostDTO dto = new ForumPostDTO();
        dto.setId(post.getId());
        dto.setActivityId(post.getActivityId());
        dto.setUserId(post.getUserId());
        dto.setUserName(user != null ? user.getUsername() : null);
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setIsExperience(post.getIsExperience());
        dto.setIsPhotoWall(post.getIsPhotoWall());
        dto.setLikeCount(post.getLikeCount());
        dto.setViewCount(post.getViewCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        List<ForumPhoto> photos = forumPhotoRepository.findByPostId(post.getId());
        dto.setPhotos(photos.stream().map(this::convertPhotoToDTO).collect(Collectors.toList()));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ForumComment> comments = forumCommentRepository.findByPostId(post.getId(), pageRequest);
        dto.setComments(comments.stream()
                .map(comment -> {
                    User commentUser = userRepository.findById(comment.getUserId()).orElse(null);
                    return convertCommentToDTO(comment, commentUser);
                })
                .collect(Collectors.toList()));

        return dto;
    }

    private ForumPhotoDTO convertPhotoToDTO(ForumPhoto photo) {
        ForumPhotoDTO dto = new ForumPhotoDTO();
        dto.setId(photo.getId());
        dto.setPhotoUrl(photo.getPhotoUrl());
        dto.setDescription(photo.getDescription());
        dto.setCreatedAt(photo.getCreatedAt());
        return dto;
    }

    private ForumCommentDTO convertCommentToDTO(ForumComment comment, User user) {
        ForumCommentDTO dto = new ForumCommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setUserName(user != null ? user.getUsername() : null);
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
