package com.volunserve.dto;

import com.volunserve.entity.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumPostCreateDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过200字符")
    private String title;

    @Size(max = 10000, message = "内容不能超过10000字符")
    private String content;

    @NotNull(message = "帖子类型不能为空")
    private PostType type;

    private Long activityId;

    private List<PostImageCreateDTO> images;
}
