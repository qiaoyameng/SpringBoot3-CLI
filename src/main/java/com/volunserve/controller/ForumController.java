package com.volunserve.controller;

import com.volunserve.dto.*;
import com.volunserve.entity.enums.PostType;
import com.volunserve.service.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ForumPostDTO>> createPost(
            @Valid @RequestBody ForumPostCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long authorId = Long.valueOf(userDetails.getUsername());
        ForumPostDTO post = forumService.createPost(dto, authorId);
        return ResponseEntity.ok(ApiResponse.success("帖子发布成功", post));
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ForumPostDTO>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody ForumPostCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        ForumPostDTO post = forumService.updatePost(postId, dto, userId);
        return ResponseEntity.ok(ApiResponse.success("帖子更新成功", post));
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        forumService.deletePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("帖子删除成功", null));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<ForumPostDTO>> getPost(@PathVariable Long postId) {
        ForumPostDTO post = forumService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PageResult<ForumPostDTO>>> listPosts(
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<ForumPostDTO> posts = forumService.listPosts(type, page, size);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @PostMapping("/posts/search")
    public ResponseEntity<ApiResponse<PageResult<ForumPostDTO>>> searchPosts(
            @RequestParam(required = false) PostType type,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<ForumPostDTO> posts = forumService.searchPosts(type, activityId, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/posts/top")
    public ResponseEntity<ApiResponse<List<ForumPostDTO>>> getTopPosts() {
        List<ForumPostDTO> posts = forumService.getTopPosts();
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/posts/essence")
    public ResponseEntity<ApiResponse<List<ForumPostDTO>>> getEssencePosts() {
        List<ForumPostDTO> posts = forumService.getEssencePosts();
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/activity/{activityId}/posts")
    public ResponseEntity<ApiResponse<PageResult<ForumPostDTO>>> getPostsByActivity(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<ForumPostDTO> posts = forumService.getPostsByActivity(activityId, page, size);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(@PathVariable Long postId) {
        forumService.likePost(postId);
        return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
    }

    @PostMapping("/posts/{postId}/top")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> setPostTop(
            @PathVariable Long postId,
            @RequestParam boolean isTop,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        forumService.setPostTop(postId, isTop, userId);
        String message = isTop ? "帖子已置顶" : "帖子已取消置顶";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PostMapping("/posts/{postId}/essence")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> setPostEssence(
            @PathVariable Long postId,
            @RequestParam boolean isEssence,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        forumService.setPostEssence(postId, isEssence, userId);
        String message = isEssence ? "帖子已设为精华" : "帖子已取消精华";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PostMapping("/comments")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PostCommentDTO>> createComment(
            @Valid @RequestBody PostCommentCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long authorId = Long.valueOf(userDetails.getUsername());
        PostCommentDTO comment = forumService.createComment(dto, authorId);
        return ResponseEntity.ok(ApiResponse.success("评论发布成功", comment));
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        forumService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success("评论删除成功", null));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<PageResult<PostCommentDTO>>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<PostCommentDTO> comments = forumService.getPostComments(postId, page, size);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<ApiResponse<Void>> likeComment(@PathVariable Long commentId) {
        forumService.likeComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
    }
}
