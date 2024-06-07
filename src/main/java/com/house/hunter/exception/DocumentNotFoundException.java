package com.house.hunter.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(final String documentName) {
        super("Document not found : " + documentName + " on path provided");
    }
    public DocumentNotFoundException() {
        super("Document is not found.");
    }

}
