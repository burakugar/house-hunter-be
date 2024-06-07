package com.house.hunter.service.impl;

import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.exception.InvalidAccountStatusException;
import com.house.hunter.exception.InvalidTokenException;
import com.house.hunter.exception.InvalidUserAuthenticationException;
import com.house.hunter.model.dto.user.UserCredentials;
import com.house.hunter.model.dto.user.UserLoginResponse;
import com.house.hunter.model.entity.RefreshToken;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.RefreshTokenRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.AuthService;
import com.house.hunter.util.BlacklistedTokenService;
import com.house.hunter.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenService blacklistedTokenService;

    public UserLoginResponse login(UserCredentials userCredentials) throws InvalidUserAuthenticationException {
        // Authenticating the user
        final Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getEmail(), userCredentials.getPassword()));
        if (authentication.isAuthenticated()) {
            final String email = authentication.getName();
            // assumed that user is present in the database as the authentication is successful
            final User user = userRepository.findByEmail(email).get();
            // check the user account status for successful login
            if (!user.getAccountStatus().equals(UserAccountStatus.valueOf("ACTIVE"))) {
                throw new InvalidAccountStatusException();
            }
            final String token = jwtUtil.buildAccessToken(user.getEmail(), user.getRole());
            final RefreshToken refreshToken = createRefreshToken(user);
            LOGGER.info("User {} logged in successfully", user.getEmail());
            return UserLoginResponse.builder().email(userCredentials.getEmail()).token(token).refreshToken(refreshToken.getToken()).build();
        }
        throw new InvalidUserAuthenticationException();
    }

    // get the refresh token, validate its expiration, if valid generate a access new token
    public String refreshToken(String token) {
        // get the refresh token from the database
        final RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        // validate the expiration of the refresh token
        verifyExpiration(refreshToken);
        // get the user from the refresh token and generate a new access token
        final User user = refreshToken.getUser();
        LOGGER.info("User {} refreshed the token", user.getEmail());
        return jwtUtil.buildAccessToken(user.getEmail(), user.getRole());
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).map(
                // if the user already has a refresh token, then update the token and expiry date
                token -> {
                    final RefreshToken newToken = jwtUtil.generateRefreshToken(user);
                    token.setToken(newToken.getToken());
                    token.setExpiryDate(newToken.getExpiryDate());
                    return refreshTokenRepository.save(token);
                }
        ).orElseGet(() -> jwtUtil.generateRefreshToken(user));
        return refreshTokenRepository.save(refreshToken);
    }

    // AuthServiceImpl.java
    @Override
    public void logout(String token) {
        // Remove the "Bearer " prefix from the token
        String tokenWithoutPrefix = token.substring(7);
        // Get the expiration date from the token
        Instant expiryDate = jwtUtil.getExpirationDateFromToken(tokenWithoutPrefix);
        // Add the token to the blacklist using the BlacklistedTokenService
        blacklistedTokenService.addToBlacklist(tokenWithoutPrefix, expiryDate);
        LOGGER.info("Token {} is blacklisted", tokenWithoutPrefix);
    }

    private void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException(" Refresh token is expired. Please make a new login..!");
        }
    }

}
