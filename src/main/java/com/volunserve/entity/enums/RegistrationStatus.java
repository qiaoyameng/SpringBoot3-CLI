package com.volunserve.entity.enums;

public enum RegistrationStatus {
    PENDING("待审核", "报名申请等待审核"),
    APPROVED("已通过", "报名申请已批准"),
    REJECTED("已拒绝", "报名申请被拒绝"),
    CANCELLED("已取消", "报名已取消");

    private final String label;
    private final String description;

    RegistrationStatus(String label, String description) {
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
