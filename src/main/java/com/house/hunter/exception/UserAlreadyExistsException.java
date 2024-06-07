package com.house.hunter.exception;

public final class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(final String email) {
        super("User with email " + email + " already exists");
    }
}
