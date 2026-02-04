package com.volunserve.activity.enumeration;

public enum ActivityStatus {
    RECRUITING("招募中"),
    ONGOING("进行中"),
    COMPLETED("已完成");

    private final String description;

    ActivityStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
