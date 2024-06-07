package com.house.hunter.exception;

public final class InvalidAccountStatusException extends RuntimeException {

    public InvalidAccountStatusException() {
        super("Account is either blocked or not activated");
    }

    public InvalidAccountStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
