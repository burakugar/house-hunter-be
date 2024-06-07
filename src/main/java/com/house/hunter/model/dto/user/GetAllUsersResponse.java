package com.house.hunter.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserVerificationStatus;
import com.house.hunter.constant.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAllUsersResponse {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserAccountStatus accountStatus;
    private UserVerificationStatus verificationStatus;
}
