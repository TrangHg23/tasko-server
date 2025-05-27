package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.config.security.JwtProvider;
import com.hoangtrang.taskoserver.dto.request.*;
import com.hoangtrang.taskoserver.dto.response.*;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.UserMapper;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.model.enums.TokenType;
import com.hoangtrang.taskoserver.repository.UserRepository;
import com.hoangtrang.taskoserver.service.AuthService;
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
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    JwtProvider jwtProvider;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorStatus.EMAIL_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(OffsetDateTime.now());

        userRepository.save(user);
        return userMapper.toRegisterResponse(user);
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = jwtProvider.isTokenValid(request.getAccessToken(), TokenType.ACCESS);

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorStatus.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

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
        jwtProvider.invalidateToken(request.getAccessToken(), TokenType.ACCESS);

        if (request.getRefreshToken() != null) {
            jwtProvider.invalidateToken(request.getRefreshToken(), TokenType.REFRESH);
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
