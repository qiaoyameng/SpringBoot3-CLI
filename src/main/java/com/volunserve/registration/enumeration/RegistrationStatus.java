package com.volunserve.registration.enumeration;

public enum RegistrationStatus {
    PENDING("待审核"),
    APPROVED("已通过"),
    REJECTED("已拒绝"),
    CANCELLED("已取消"),
    CHECKED_IN("已签到"),
    CHECKED_OUT("已签退");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
