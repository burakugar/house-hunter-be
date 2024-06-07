package com.house.hunter.model.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class RefreshTokenResponseDTO {
    private String token;
}
