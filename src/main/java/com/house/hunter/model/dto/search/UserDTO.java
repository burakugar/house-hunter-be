package com.house.hunter.model.dto.search;

import lombok.Data;

import java.util.UUID;
@Data
public class UserDTO {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
}
