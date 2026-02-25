package com.volunserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageDTO {
    private Long id;
    private String imageUrl;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
