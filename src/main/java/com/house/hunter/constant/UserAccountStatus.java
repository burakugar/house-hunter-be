package com.house.hunter.constant;

public enum UserAccountStatus {
    ACTIVE,
    NOT_ACTIVATED,
    BLOCKED;
    // Constant regex pattern
    public static final String PATTERN = "ACTIVE|NOT ACTIVATED|BLOCKED";
}
