package com.house.hunter.exception;

public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException(final String propertyId) {
        super("Property not found : " + propertyId);
    }

    public PropertyNotFoundException() {
        super("Property is not found.");
    }
}

