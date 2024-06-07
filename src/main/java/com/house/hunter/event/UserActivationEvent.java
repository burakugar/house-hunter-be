package com.house.hunter.event;

import com.house.hunter.model.entity.User;
import lombok.Data;

@Data
public class UserActivationEvent {
    private final User user;
}
