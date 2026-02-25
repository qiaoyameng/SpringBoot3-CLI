package com.volunserve.entity.enums;

public enum ActivityCategory {
    ENVIRONMENT("环保", "参与环境保护、垃圾分类、植树造林等活动"),
    ELDERLY_CARE("助老", "关爱老年人、陪伴聊天、生活照料等服务"),
    EDUCATION("教育", "支教助学、课业辅导、技能培训等活动"),
    MEDICAL("医疗", "健康宣教、义诊服务、医疗辅助等活动");

    private final String label;
    private final String description;

    ActivityCategory(String label, String description) {
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
