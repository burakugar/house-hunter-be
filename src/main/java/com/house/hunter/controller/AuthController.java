package com.house.hunter.controller;

import com.house.hunter.exception.InvalidUserAuthenticationException;
import com.house.hunter.model.dto.token.RefreshTokenRequestDTO;
import com.house.hunter.model.dto.token.RefreshTokenResponseDTO;
import com.house.hunter.model.dto.user.UserCredentials;
import com.house.hunter.model.dto.user.UserLoginResponse;
import com.house.hunter.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Validated
@Tag(name = "Auth Controller", description = "Endpoints for authentication management")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/refreshToken", produces = "application/json")
    @Operation(summary = "Refresh token")
    @ResponseStatus(HttpStatus.OK)
    public RefreshTokenResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return RefreshTokenResponseDTO.builder().token(authService.refreshToken(refreshTokenRequestDTO.getToken())).build();
    }

    @PostMapping(value = "/login", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login user")
    public UserLoginResponse login(@RequestBody UserCredentials userCredentials) throws InvalidUserAuthenticationException {
        return authService.login(userCredentials);
    }
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

}
