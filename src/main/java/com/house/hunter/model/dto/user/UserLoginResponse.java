package com.house.hunter.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginResponse {
    private String email;
    private String token;
    private String refreshToken;
}
