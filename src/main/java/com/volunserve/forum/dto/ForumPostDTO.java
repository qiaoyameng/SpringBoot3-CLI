package com.volunserve.forum.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ForumPostDTO {
    private Long id;
    private Long activityId;
    private Long userId;
    private String userName;
    private String title;
    private String content;
    private Boolean isExperience;
    private Boolean isPhotoWall;
    private Integer likeCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ForumPhotoDTO> photos;
    private List<ForumCommentDTO> comments;
}
