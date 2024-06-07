package com.house.hunter.exception;

public final class PropertyNotVerifiedException extends RuntimeException {
    public PropertyNotVerifiedException() {
        super("Selected property is not verified yet");
    }
}
