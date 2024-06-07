package com.house.hunter.event;

import com.house.hunter.model.entity.Property;

public class PropertyRejectionEvent {
    private final Property property;

    public PropertyRejectionEvent(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}

