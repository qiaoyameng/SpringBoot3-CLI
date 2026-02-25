package com.volunserve.entity.enums;

public enum PostType {
    EXPERIENCE("经验分享", "志愿者分享活动经验和心得"),
    PHOTO("照片墙", "活动照片分享"),
    DISCUSSION("讨论", "话题讨论");

    private final String label;
    private final String description;

    PostType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
