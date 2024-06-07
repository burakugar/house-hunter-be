package com.house.hunter.exception;

public class IllegalAccessRequestException extends RuntimeException {
    public IllegalAccessRequestException(final String message) {
        super(message);
    }

    public IllegalAccessRequestException() {
        super("Illegal access request to the resource.");
    }
}
