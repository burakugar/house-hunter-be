package com.house.hunter.constant;

public enum UserRole {
    ADMIN,
    LANDLORD,
    TENANT;

    // Constant regex pattern
    public static final String PATTERN = "ADMIN|LANDLORD|TENANT";
}
