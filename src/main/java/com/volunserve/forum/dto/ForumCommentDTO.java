package com.volunserve.forum.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumCommentDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;
}
