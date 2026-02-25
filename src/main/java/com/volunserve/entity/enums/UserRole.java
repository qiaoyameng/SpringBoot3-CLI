package com.volunserve.entity.enums;

public enum UserRole {
    VOLUNTEER("志愿者", "普通志愿者用户"),
    ORGANIZER("组织者", "活动组织者"),
    ADMIN("管理员", "系统管理员");

    private final String label;
    private final String description;

    UserRole(String label, String description) {
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
