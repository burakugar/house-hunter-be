package com.house.hunter.constant;

public enum IsFurnished {
    FURNISHED,
    SEMI_FURNISHED,
    UNFURNISHED;

    // Constant regular expression that matches the enum values
    public static final String PATTERN = "FURNISHED|SEMI_FURNISHED|UNFURNISHED";

    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
