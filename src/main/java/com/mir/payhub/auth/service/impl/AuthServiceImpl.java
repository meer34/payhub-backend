package com.mir.payhub.auth.service.impl;

import com.mir.payhub.auth.dto.request.*;
import com.mir.payhub.auth.dto.response.AuthResponse;
import com.mir.payhub.auth.dto.response.UserResponse;
import com.mir.payhub.auth.entity.RefreshToken;
import com.mir.payhub.auth.service.AuthService;
import com.mir.payhub.auth.service.RefreshTokenService;
import com.mir.payhub.common.enums.RoleType;
import com.mir.payhub.exception.BadRequestException;
import com.mir.payhub.exception.ResourceNotFoundException;
import com.mir.payhub.exception.UnauthorizedException;
import com.mir.payhub.security.jwt.JwtService;
import com.mir.payhub.security.service.CustomUserPrincipal;
import com.mir.payhub.user.entity.Role;
import com.mir.payhub.user.entity.User;
import com.mir.payhub.user.mapper.UserMapper;
import com.mir.payhub.user.repository.RoleRepository;
import com.mir.payhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (request.getMobile() != null
                && userRepository.existsByMobile(request.getMobile())) {
            throw new BadRequestException("Mobile already registered");
        }

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Default role not found"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountNonLocked(true)
                .emailVerified(false)
                .mobileVerified(false)
                .build();

        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .user(userMapper.toResponse(savedUser))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        /*
         * Lock the user BEFORE touching refresh_tokens.
         * This establishes the lock order:
         *
         * users -> refresh_tokens
         */
        User user = userRepository.findWithLockByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);

        refreshTokenService.revoke(user);

        String refreshToken = refreshTokenService.create(
                user,
                "Unknown Device",
                "127.0.0.1"
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String token = request.getRefreshToken();

        /*
         * First find the token without a lock so that we can identify
         * the owning user.
         */
        RefreshToken existingToken =
                refreshTokenService.findByToken(token);

        if (existingToken == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (existingToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        User user = userRepository.findWithLockById(
                existingToken.getUser().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        /*
         * User is now locked.
         * From this point onward the lock order is:
         *
         * users -> refresh_tokens
         */
        RefreshToken refreshToken =
                refreshTokenService.findByTokenWithLock(token);

        if (!refreshTokenService.isValid(token, refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String deviceName = refreshToken.getDeviceName();
        String ipAddress = refreshToken.getIpAddress();

        refreshTokenService.revoke(refreshToken);

        String accessToken =
                jwtService.generateAccessToken(user);

        String newRefreshToken =
                refreshTokenService.create(
                        user,
                        deviceName,
                        ipAddress
                );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.findByToken(request.getRefreshToken());

        if (refreshToken == null) {
            return;
        }

        refreshTokenService.revoke(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        User user = principal.getUser();

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toSet())
                )
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        User user = principal.getUser();

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new UnauthorizedException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword()
        )) {
            throw new BadRequestException(
                    "New password must be different from current password."
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        refreshTokenService.revoke(user);
    }
}