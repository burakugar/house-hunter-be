package com.house.hunter.model.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserPasswordUpdateDTO {
    @NotEmpty(message = "Email is required")
    private String email;
    @NotEmpty(message = "Old password is required")
    private String oldPassword;
    @NotEmpty(message = "New password is required")
    private String newPassword;
}
