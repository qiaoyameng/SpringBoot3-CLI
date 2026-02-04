package com.volunserve.forum.controller;

import com.volunserve.common.response.Result;
import com.volunserve.forum.dto.*;
import com.volunserve.forum.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Tag(name = "论坛管理", description = "活动论坛接口，支持经验分享和照片墙")
public class ForumController {

    private final ForumService forumService;

    @PostMapping("/posts")
    @Operation(summary = "创建帖子", description = "创建新的论坛帖子，支持经验分享或照片墙")
    public Result<ForumPostDTO> createPost(@Valid @RequestBody ForumPostCreateDTO dto) {
        return Result.success(forumService.createPost(dto));
    }

    @PutMapping("/posts/{postId}")
    @Operation(summary = "更新帖子", description = "更新指定的论坛帖子")
    public Result<ForumPostDTO> updatePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody ForumPostUpdateDTO dto) {
        return Result.success(forumService.updatePost(postId, userId, dto));
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "删除帖子", description = "删除指定的论坛帖子")
    public Result<Void> deletePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        forumService.deletePost(postId, userId);
        return Result.success();
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "获取帖子详情", description = "根据ID获取帖子详细信息")
    public Result<ForumPostDTO> getPostById(
            @Parameter(description = "帖子ID") @PathVariable Long postId) {
        return Result.success(forumService.getPostById(postId));
    }

    @GetMapping("/posts")
    @Operation(summary = "获取所有帖子", description = "分页获取所有论坛帖子")
    public Result<Page<ForumPostDTO>> getAllPosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumService.getAllPosts(page, size));
    }

    @GetMapping("/posts/experience")
    @Operation(summary = "获取经验分享", description = "分页获取所有经验分享帖子")
    public Result<Page<ForumPostDTO>> getExperiencePosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumService.getExperiencePosts(page, size));
    }

    @GetMapping("/posts/photo-wall")
    @Operation(summary = "获取照片墙", description = "分页获取所有照片墙帖子")
    public Result<Page<ForumPostDTO>> getPhotoWallPosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumService.getPhotoWallPosts(page, size));
    }

    @GetMapping("/activities/{activityId}/posts")
    @Operation(summary = "获取活动相关帖子", description = "根据活动ID获取相关帖子")
    public Result<Page<ForumPostDTO>> getPostsByActivity(
            @Parameter(description = "活动ID") @PathVariable Long activityId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumService.getPostsByActivity(activityId, page, size));
    }

    @GetMapping("/users/{userId}/posts")
    @Operation(summary = "获取用户帖子", description = "根据用户ID获取其发布的帖子")
    public Result<Page<ForumPostDTO>> getPostsByUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumService.getPostsByUser(userId, page, size));
    }

    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "点赞帖子", description = "为指定帖子点赞")
    public Result<Void> likePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId) {
        forumService.likePost(postId);
        return Result.success();
    }

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "添加评论", description = "为指定帖子添加评论")
    public Result<ForumCommentDTO> addComment(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Valid @RequestBody ForumCommentCreateDTO dto) {
        return Result.success(forumService.addComment(postId, userId, dto));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "删除评论", description = "删除指定的评论")
    public Result<Void> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        forumService.deleteComment(commentId, userId);
        return Result.success();
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "获取帖子评论", description = "分页获取指定帖子的评论")
    public Result<Page<ForumCommentDTO>> getCommentsByPost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumService.getCommentsByPost(postId, page, size));
    }
}
