package com.volunserve.dto;

import com.volunserve.entity.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumPostDTO {
    private Long id;
    private String title;
    private String content;
    private PostType type;
    private String typeLabel;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Long activityId;
    private String activityTitle;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private List<PostImageDTO> images;
    private Boolean isTop;
    private Boolean isEssence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
