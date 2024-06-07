package com.house.hunter.exception;

public final class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {super("User not found");
    }
    public UserNotFoundException(final String email) {
        super("User with email " + email + " not found");
    }
}
