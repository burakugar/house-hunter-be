package com.house.hunter.exception;

public final class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException() {
        super("Provided verification token is either expired or invalid.");
    }
}
