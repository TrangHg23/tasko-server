package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.config.security.JwtProvider;
import com.hoangtrang.taskoserver.dto.auth.*;
import com.hoangtrang.taskoserver.dto.auth.IntrospectRequest;
import com.hoangtrang.taskoserver.dto.auth.IntrospectResponse;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.UserMapper;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.model.enums.TokenType;
import com.hoangtrang.taskoserver.repository.UserRepository;
import com.hoangtrang.taskoserver.service.AuthService;
import com.hoangtrang.taskoserver.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    CategoryService categoryService;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    JwtProvider jwtProvider;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorStatus.EMAIL_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(OffsetDateTime.now());

        User savedUser = userRepository.save(user);

        categoryService.createDefaultCategories(savedUser);

        return userMapper.toRegisterResponse(savedUser);
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = jwtProvider.isTokenValid(request.accessToken(), TokenType.ACCESS);

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorStatus.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(request.password(), user.getPassword());

        if(!authenticated)
            throw new AppException(ErrorStatus.UNAUTHENTICATED);

        var accessTokenResponse = jwtProvider.generateToken(user, TokenType.ACCESS);
        var refreshTokenResponse = jwtProvider.generateToken(user, TokenType.REFRESH);

        UserInfo userInfo = userMapper.toUserInfo(user);

        return LoginResponse.builder()
                .authenticated(true)
                .accessToken(accessTokenResponse.getToken())
                .refreshToken(refreshTokenResponse.getToken())
                .tokenType("Bearer")
                .expiresAt(accessTokenResponse.getExpiresAt())
                .user(userInfo)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        jwtProvider.invalidateToken(request.accessToken(), TokenType.ACCESS);

        if (request.refreshToken() != null) {
            jwtProvider.invalidateToken(request.refreshToken(), TokenType.REFRESH);
        }

        log.info("Logout successful.");
    }


    private User validateRefreshToken(String refreshToken) {
        try {
            var jwt = jwtProvider.verifyToken(refreshToken, TokenType.REFRESH);
            String email = jwt.getSubject();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorStatus.USER_NOT_EXISTED));
        } catch (Exception e) {
            throw new AppException(ErrorStatus.UNAUTHENTICATED);
        }
    }

    @Override
    public RefreshResponse refreshAccessToken(String refreshToken) {
        var user = validateRefreshToken(refreshToken);

        TokenResponse newAccessToken = jwtProvider.generateToken(user, TokenType.ACCESS);

        return RefreshResponse.builder()
                .authenticated(true)
                .accessToken(newAccessToken.getToken())
                .tokenType("Bearer")
                .expiresAt(newAccessToken.getExpiresAt())
                .build();
    }

}
