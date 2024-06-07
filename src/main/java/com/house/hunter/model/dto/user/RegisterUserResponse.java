package com.house.hunter.model.dto.user;

import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserRole;
import com.house.hunter.constant.UserVerificationStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterUserResponse {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserAccountStatus status;
    private UserVerificationStatus verificationStatus;
}
