package com.dsa360.api.constants;

public enum ApprovalStatus {
    PENDING("Pending"), // 0 default
    UNDER_REVIEW("Under Review"), //1
    APPROVED("Approved"), //2
    REJECTED("Rejected"); //3

    private final String value;

    ApprovalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
