package com.volunserve.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ForumPostCreateDTO {
    private Long activityId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private Boolean isExperience = false;

    private Boolean isPhotoWall = false;

    private List<String> photoUrls;
}
