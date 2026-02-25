package com.volunserve.service;

import com.volunserve.dto.*;
import com.volunserve.entity.*;
import com.volunserve.entity.enums.PostType;
import com.volunserve.exception.BusinessException;
import com.volunserve.exception.ResourceNotFoundException;
import com.volunserve.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumPostRepository forumPostRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    @Transactional
    public ForumPostDTO createPost(ForumPostCreateDTO dto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        ForumPost.ForumPostBuilder postBuilder = ForumPost.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType())
                .author(author)
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .isTop(false)
                .isEssence(false)
                .enabled(true);

        if (dto.getActivityId() != null) {
            Activity activity = activityRepository.findById(dto.getActivityId())
                    .orElseThrow(() -> new ResourceNotFoundException("活动不存在"));
            postBuilder.activity(activity);
        }

        ForumPost post = postBuilder.build();
        ForumPost saved = forumPostRepository.save(post);

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<PostImage> images = dto.getImages().stream().map(imgDto ->
                PostImage.builder()
                        .post(saved)
                        .imageUrl(imgDto.getImageUrl())
                        .description(imgDto.getDescription())
                        .sortOrder(imgDto.getSortOrder())
                        .build()
            ).collect(Collectors.toList());
            postImageRepository.saveAll(images);
            saved.setImages(images);
        }

        return convertToDTO(saved);
    }

    @Transactional
    public ForumPostDTO updatePost(Long postId, ForumPostCreateDTO dto, Long userId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("帖子不存在"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new BusinessException("无权编辑该帖子");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        if (dto.getImages() != null) {
            postImageRepository.deleteByPostId(postId);
            List<PostImage> images = dto.getImages().stream().map(imgDto ->
                PostImage.builder()
                        .post(post)
                        .imageUrl(imgDto.getImageUrl())
                        .description(imgDto.getDescription())
                        .sortOrder(imgDto.getSortOrder())
                        .build()
            ).collect(Collectors.toList());
            postImageRepository.saveAll(images);
            post.setImages(images);
        }

        ForumPost saved = forumPostRepository.save(post);
        return convertToDTO(saved);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("帖子不存在"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new BusinessException("无权删除该帖子");
        }

        post.setEnabled(false);
        forumPostRepository.save(post);
    }

    @Transactional(readOnly = true)
    public ForumPostDTO getPost(Long postId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("帖子不存在"));

        if (!post.getEnabled()) {
            throw new ResourceNotFoundException("帖子已被删除");
        }

        forumPostRepository.incrementViewCount(postId);
        post.setViewCount(post.getViewCount() + 1);

        return convertToDTO(post);
    }

    @Transactional(readOnly = true)
    public PageResult<ForumPostDTO> listPosts(PostType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "isTop", "createdAt"));
        Page<ForumPost> postPage;

        if (type != null) {
            postPage = forumPostRepository.findByTypeAndEnabledTrue(type, pageable);
        } else {
            postPage = forumPostRepository.findAll(pageable);
        }

        return convertToPageResult(postPage);
    }

    @Transactional(readOnly = true)
    public PageResult<ForumPostDTO> searchPosts(PostType type, Long activityId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ForumPost> postPage = forumPostRepository.searchPosts(type, activityId, keyword, pageable);
        return convertToPageResult(postPage);
    }

    @Transactional(readOnly = true)
    public List<ForumPostDTO> getTopPosts() {
        return forumPostRepository.findTopPosts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumPostDTO> getEssencePosts() {
        return forumPostRepository.findEssencePosts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResult<ForumPostDTO> getPostsByActivity(Long activityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ForumPost> postPage = forumPostRepository.findByActivityIdAndEnabledTrue(activityId, pageable);
        return convertToPageResult(postPage);
    }

    @Transactional
    public void likePost(Long postId) {
        forumPostRepository.incrementLikeCount(postId);
    }

    @Transactional
    public PostCommentDTO createComment(PostCommentCreateDTO dto, Long authorId) {
        ForumPost post = forumPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("帖子不存在"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        PostComment.PostCommentBuilder commentBuilder = PostComment.builder()
                .post(post)
                .author(author)
                .content(dto.getContent())
                .likeCount(0)
                .enabled(true);

        if (dto.getParentId() != null) {
            PostComment parent = postCommentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父评论不存在"));
            commentBuilder.parent(parent);
        }

        PostComment comment = commentBuilder.build();
        PostComment saved = postCommentRepository.save(comment);

        forumPostRepository.incrementCommentCount(post.getId());

        return convertCommentToDTO(saved);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("评论不存在"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new BusinessException("无权删除该评论");
        }

        comment.setEnabled(false);
        postCommentRepository.save(comment);

        forumPostRepository.decrementCommentCount(comment.getPost().getId());
    }

    @Transactional(readOnly = true)
    public PageResult<PostCommentDTO> getPostComments(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostComment> commentPage = postCommentRepository.findByPostIdAndParentIsNullAndEnabledTrue(postId, pageable);

        List<PostCommentDTO> commentDTOs = commentPage.getContent().stream().map(comment -> {
            PostCommentDTO dto = convertCommentToDTO(comment);
            List<PostComment> replies = postCommentRepository.findByPostIdAndParentIdAndEnabledTrue(postId, comment.getId());
            dto.setReplies(replies.stream().map(this::convertCommentToDTO).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        return PageResult.<PostCommentDTO>builder()
                .content(commentDTOs)
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .currentPage(commentPage.getNumber())
                .pageSize(commentPage.getSize())
                .first(commentPage.isFirst())
                .last(commentPage.isLast())
                .empty(commentPage.isEmpty())
                .build();
    }

    @Transactional
    public void likeComment(Long commentId) {
        postCommentRepository.incrementLikeCount(commentId);
    }

    @Transactional
    public void setPostTop(Long postId, boolean isTop, Long userId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("帖子不存在"));
        post.setIsTop(isTop);
        forumPostRepository.save(post);
    }

    @Transactional
    public void setPostEssence(Long postId, boolean isEssence, Long userId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("帖子不存在"));
        post.setIsEssence(isEssence);
        forumPostRepository.save(post);
    }

    private ForumPostDTO convertToDTO(ForumPost post) {
        ForumPostDTO dto = ForumPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .type(post.getType())
                .typeLabel(post.getType().getLabel())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getRealName() != null ? post.getAuthor().getRealName() : post.getAuthor().getUsername())
                .authorAvatar(post.getAuthor().getAvatar())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isTop(post.getIsTop())
                .isEssence(post.getIsEssence())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();

        if (post.getActivity() != null) {
            dto.setActivityId(post.getActivity().getId());
            dto.setActivityTitle(post.getActivity().getTitle());
        }

        if (post.getImages() != null) {
            dto.setImages(post.getImages().stream().map(img ->
                PostImageDTO.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .description(img.getDescription())
                        .sortOrder(img.getSortOrder())
                        .createdAt(img.getCreatedAt())
                        .build()
            ).collect(Collectors.toList()));
        }

        return dto;
    }

    private PostCommentDTO convertCommentToDTO(PostComment comment) {
        PostCommentDTO dto = PostCommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getRealName() != null ? comment.getAuthor().getRealName() : comment.getAuthor().getUsername())
                .authorAvatar(comment.getAuthor().getAvatar())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();

        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
        }

        return dto;
    }

    private PageResult<ForumPostDTO> convertToPageResult(Page<ForumPost> page) {
        return PageResult.<ForumPostDTO>builder()
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
