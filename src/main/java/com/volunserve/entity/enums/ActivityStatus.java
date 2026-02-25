package com.volunserve.entity.enums;

public enum ActivityStatus {
    RECRUITING("招募中", "活动正在招募志愿者"),
    IN_PROGRESS("进行中", "活动正在进行中"),
    COMPLETED("已完成", "活动已结束"),
    CANCELLED("已取消", "活动已取消");

    private final String label;
    private final String description;

    ActivityStatus(String label, String description) {
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
