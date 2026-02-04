package com.volunserve.forum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForumCommentCreateDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
