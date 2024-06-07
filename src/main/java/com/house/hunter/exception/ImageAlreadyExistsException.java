package com.house.hunter.exception;

public final class ImageAlreadyExistsException extends RuntimeException {
    public ImageAlreadyExistsException(String message) {
        super(message);
    }

    public ImageAlreadyExistsException() {
        super("Image already exists");
    }
}
