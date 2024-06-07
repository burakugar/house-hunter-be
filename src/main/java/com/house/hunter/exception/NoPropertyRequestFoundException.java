package com.house.hunter.exception;

public final class NoPropertyRequestFoundException extends RuntimeException {

    public NoPropertyRequestFoundException() {
        super("Could not find any property requests");
    }
}
