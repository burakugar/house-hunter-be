package com.house.hunter.exception;

import javax.naming.AuthenticationException;

public final class InvalidUserAuthenticationException extends AuthenticationException {
    public InvalidUserAuthenticationException() {
        super("Password or email is wrong");
    }
}
