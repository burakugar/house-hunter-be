package com.house.hunter.exception;

public final class DocumentAlreadyExistsException extends RuntimeException {
    public DocumentAlreadyExistsException(final String documentName) {
        super("Document already exists : " + documentName);
    }
}
