package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.request.IntrospectRequest;
import com.hoangtrang.taskoserver.dto.request.LoginRequest;
import com.hoangtrang.taskoserver.dto.request.LogoutRequest;
import com.hoangtrang.taskoserver.dto.request.RegisterRequest;
import com.hoangtrang.taskoserver.dto.response.*;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.UserMapper;
import com.hoangtrang.taskoserver.model.InvalidatedToken;
import com.hoangtrang.taskoserver.model.User;
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
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
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

        TokenResponse tokenResponse = generateToken(user.getEmail());

        UserInfo userInfo = userMapper.toUserInfo(user);

        return LoginResponse.builder()
                .authenticated(true)
                .accessToken(tokenResponse.getToken())
                .tokenType("Bearer")
                .expiresAt(tokenResponse.getExpiresAt())
                .user(userInfo)
                .build();

    }

    private TokenResponse generateToken(String email) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Instant expirationTime = Instant.now().plus(1, ChronoUnit.HOURS);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .issuer("hoangtrang.com")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(expirationTime.toEpochMilli()))
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
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if(!(verified && expireTime.after(new Date())))
            throw new AppException(ErrorStatus.UNAUTHENTICATED);

        if(invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorStatus.UNAUTHENTICATED);

        return signedJWT;
    }
}
