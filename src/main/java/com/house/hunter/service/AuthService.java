package com.house.hunter.service;

import com.house.hunter.exception.InvalidUserAuthenticationException;
import com.house.hunter.model.dto.user.UserCredentials;
import com.house.hunter.model.dto.user.UserLoginResponse;
import com.house.hunter.model.entity.User;

public interface AuthService {
    UserLoginResponse login(UserCredentials userCredentials) throws InvalidUserAuthenticationException;

    String refreshToken(String token);

    void logout(String token);

}
