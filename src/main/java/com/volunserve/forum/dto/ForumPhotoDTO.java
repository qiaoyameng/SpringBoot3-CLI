package com.volunserve.forum.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumPhotoDTO {
    private Long id;
    private String photoUrl;
    private String description;
    private LocalDateTime createdAt;
}
