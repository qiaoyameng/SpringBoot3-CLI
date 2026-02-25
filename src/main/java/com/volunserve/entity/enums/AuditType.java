package com.volunserve.entity.enums;

public enum AuditType {
    AUTO("自动审核", "系统自动审核通过"),
    MANUAL("人工审核", "需要管理员人工审核");

    private final String label;
    private final String description;

    AuditType(String label, String description) {
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
