package com.house.hunter.constant;

public enum UserVerificationStatus {
    NOT_VERIFIED,
    PENDING_VERIFICATION,
    VERIFIED,
    REJECTED;
    // Constant regex pattern
    public static final String PATTERN = "NOT VERIFIED|PENDING VERIFICATION|VERIFIED|REJECTED";
}
