package com.volunserve.forum.dto;

import lombok.Data;
import java.util.List;

@Data
public class ForumPostUpdateDTO {
    private String title;
    private String content;
    private List<String> photoUrls;
}
