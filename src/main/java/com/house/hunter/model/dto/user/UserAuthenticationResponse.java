package com.house.hunter.model.dto.user;

import lombok.Data;

@Data
public class UserAuthenticationResponse {
    private String email;
    private String role;
    private String status;
}
