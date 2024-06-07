package com.house.hunter.exception;

public final class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message) {
        super(message);
    }

    public ImageNotFoundException() {
        super("Image not found");
    }

}
