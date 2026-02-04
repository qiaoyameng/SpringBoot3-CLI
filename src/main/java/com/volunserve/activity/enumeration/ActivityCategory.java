package com.volunserve.activity.enumeration;

public enum ActivityCategory {
    ENVIRONMENTAL("环保"),
    ELDERLY_ASSISTANCE("助老"),
    EDUCATION("教育"),
    MEDICAL("医疗");

    private final String description;

    ActivityCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
