package com.house.hunter.constant;

public enum DocumentType {
    PASSPORT,
    ID_CARD,
    DRIVER_LICENSE,
    RESIDENCE_PERMIT,
    OWNERSHIP_DOCUMENT,
    OTHER;
    public static final String PATTERN = "PASSPORT|ID_CARD|DRIVER_LICENSE|RESIDENCE_PERMIT|OWNERSHIP_DOCUMENT|OTHER";

    public static boolean contains(String documentType) {
        for (DocumentType type : DocumentType.values()) {
            if (type.name().equals(documentType)) {
                return true;
            }
        }
        return false;
    }
}
