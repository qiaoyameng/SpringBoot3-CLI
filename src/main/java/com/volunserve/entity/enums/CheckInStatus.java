package com.volunserve.entity.enums;

public enum CheckInStatus {
    NOT_CHECKED_IN("未签到", "志愿者尚未签到"),
    CHECKED_IN("已签到", "志愿者已签到"),
    CHECKED_OUT("已签出", "志愿者已签出");

    private final String label;
    private final String description;

    CheckInStatus(String label, String description) {
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
