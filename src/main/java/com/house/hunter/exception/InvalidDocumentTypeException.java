package com.house.hunter.exception;

import com.house.hunter.constant.DocumentType;

public final class InvalidDocumentTypeException extends RuntimeException {

    public InvalidDocumentTypeException() {
        super("Document type should be one of the following: " + DocumentType.PATTERN + ". ");
    }

}
