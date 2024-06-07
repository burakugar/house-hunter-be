package com.house.hunter.event;

import com.house.hunter.model.entity.Property;

public class PropertyVerificationEvent {
    private final Property property;

    public PropertyVerificationEvent(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}
