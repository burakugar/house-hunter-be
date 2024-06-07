package com.house.hunter.exception;

public final class PropertyAlreadyExistsException extends RuntimeException {
    public PropertyAlreadyExistsException() {
        super("Property already exists");
    }
}
