package com.house.hunter.constant;

public enum AdType {
    RENTAL,
    SALE;
    // Constant regex pattern
    public static final String PATTERN = "RENTAL|SALE";
    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
