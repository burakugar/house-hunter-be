package com.house.hunter.constant;

public enum RequestFormSubject {
    QUESTION,
    COMPLAINT,
    VIEWING,;

    // Constant regular expression that matches the enum values
    public static final String PATTERN = "QUESTION|COMPLAINT|VIEWING";
    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
