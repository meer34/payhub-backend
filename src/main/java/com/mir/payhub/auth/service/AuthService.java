package com.mir.payhub.auth.service;

import com.mir.payhub.auth.dto.request.*;
import com.mir.payhub.auth.dto.response.AuthResponse;
import com.mir.payhub.auth.dto.response.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    UserResponse me();

    void changePassword(ChangePasswordRequest request);

}