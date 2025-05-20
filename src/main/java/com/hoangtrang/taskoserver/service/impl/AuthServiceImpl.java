package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.request.*;
import com.hoangtrang.taskoserver.dto.response.*;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.UserMapper;
import com.hoangtrang.taskoserver.model.InvalidatedToken;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.model.enums.TokenType;
import com.hoangtrang.taskoserver.repository.InvalidatedTokenRepository;
import com.hoangtrang.taskoserver.repository.UserRepository;
import com.hoangtrang.taskoserver.service.AuthService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${spring.security.jwt.secret-key}")
    protected String SIGNER_KEY;

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
        var token = request.getAccessToken();
        boolean isValid = true;
        try {
            verifyToken(token, TokenType.ACCESS);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }

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

        var accessTokenResponse = generateToken(user, TokenType.ACCESS);
        var refreshTokenResponse = generateToken(user, TokenType.REFRESH);

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

    private TokenResponse generateToken(User user, TokenType tokenType) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Instant expirationTime = Instant.now().plus(tokenType.getHoursToExpire(), ChronoUnit.HOURS);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("hoangtrang.com")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(expirationTime.toEpochMilli()))
                .claim("token_type", tokenType.name())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return new TokenResponse(jwsObject.serialize(), expirationTime.toEpochMilli());
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(LogoutRequest request) {
        try {
            invalidateToken(request.getAccessToken(), TokenType.ACCESS);
            if(request.getRefreshToken() != null) {
                invalidateToken(request.getRefreshToken(), TokenType.REFRESH);
            }
            log.info("Logout successful for user.");
        } catch (AppException | JOSEException | ParseException e) {
            log.error("Logout failed: invalid token", e);
            throw new AppException(ErrorStatus.UNAUTHENTICATED);
        }
    }

    private void invalidateToken(String token, TokenType tokenType) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        log.info("{} token with jti={} invalidated successfully", tokenType.name(), jti);
    }

    private SignedJWT verifyToken(String token, TokenType expectedType) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        // Xác thực signed key
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        boolean verified = signedJWT.verify(verifier);
        if (!verified) {
            throw new AppException(ErrorStatus.UNAUTHENTICATED);
        }

        // Kiểm tra thời gian hết hạn (thêm khoảng leeway 30 giây)
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null || expirationTime.before(new Date(System.currentTimeMillis() - 30000))) {
            throw new AppException(
                    expectedType == TokenType.ACCESS ? ErrorStatus.ACCESS_TOKEN_EXPIRED : ErrorStatus.REFRESH_TOKEN_EXPIRED
            );
        }


        String tokenType = claims.getStringClaim("token_type");
        if (tokenType == null || !tokenType.equals(expectedType.name())) {
            throw new AppException(ErrorStatus.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(claims.getJWTID())) {
            throw new AppException(ErrorStatus.ACCESS_TOKEN_EXPIRED);
        }

        return signedJWT;
    }

    private User validateRefreshToken(String refreshToken) {
        try {
            SignedJWT signedJWT = verifyToken(refreshToken, TokenType.REFRESH);
            String email = signedJWT.getJWTClaimsSet().getSubject();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorStatus.USER_NOT_EXISTED));
        } catch (Exception e) {
            throw new AppException(ErrorStatus.UNAUTHENTICATED);
        }
    }

    @Override
    public RefreshResponse refreshAccessToken(String refreshToken) {
        var user = validateRefreshToken(refreshToken);

        TokenResponse newAccessToken = generateToken(user, TokenType.ACCESS);

        return RefreshResponse.builder()
                .authenticated(true)
                .accessToken(newAccessToken.getToken())
                .tokenType("Bearer")
                .expiresAt(newAccessToken.getExpiresAt())
                .build();
    }

}
