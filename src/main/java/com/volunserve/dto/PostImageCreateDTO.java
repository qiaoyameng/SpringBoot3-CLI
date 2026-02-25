package com.volunserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageCreateDTO {

    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    @Size(max = 200, message = "图片描述不能超过200字符")
    private String description;

    private Integer sortOrder;
}
