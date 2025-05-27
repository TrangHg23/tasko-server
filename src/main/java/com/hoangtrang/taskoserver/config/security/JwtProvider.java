package com.hoangtrang.taskoserver.config.security;

import com.hoangtrang.taskoserver.dto.response.TokenResponse;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.model.InvalidatedToken;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.model.enums.TokenType;
import com.hoangtrang.taskoserver.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public TokenResponse generateToken(User user, TokenType tokenType) {
        Instant now = Instant.now();
        Instant expiry = now.plus(tokenType.getHoursToExpire(), ChronoUnit.HOURS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("hoangtrang.com")
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(expiry)
                .claim("token_type", tokenType.name())
                .claim("jti", UUID.randomUUID().toString())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token, expiry.toEpochMilli());
    }


    public Jwt verifyToken(String token, TokenType expectedType) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(token);
        } catch (JwtException e) {
            log.warn("Token decode failed: {}", e.getMessage());
            throw new AppException(ErrorStatus.UNAUTHENTICATED);
        }

        String jti = jwt.getClaim("jti");
        if (invalidatedTokenRepository.existsById(jti)) {
            log.warn("Token has already been invalidated.");
            throw new AppException(ErrorStatus.ACCESS_TOKEN_EXPIRED);
        }

        Instant expiration = jwt.getExpiresAt();
        if (expiration == null || expiration.isBefore(Instant.now())) {
            throw new AppException(expectedType == TokenType.ACCESS ?
                    ErrorStatus.ACCESS_TOKEN_EXPIRED : ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        String actualType = jwt.getClaim("token_type");
        if (!expectedType.name().equals(actualType)) {
            log.warn("Token type mismatch. Expected: {}, Actual: {}", expectedType.name(), actualType);
            throw new AppException(ErrorStatus.UNAUTHENTICATED);
        }

        return jwt;
    }


    public void invalidateToken(String token, TokenType expectedType) {
        try {
            Jwt jwt = verifyToken(token, expectedType);
            String jti = jwt.getClaim("jti");
            Instant expiry = jwt.getExpiresAt();

            InvalidatedToken invalidToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(Date.from(expiry))
                    .build();
            if(!invalidatedTokenRepository.existsById(jti)) {
                invalidatedTokenRepository.save(invalidToken);
                log.info("Token invalidated successfully: {}", jti);
            } else {
                log.info("Token already invalidated: {}", jti);
            }

        } catch(AppException e) {
            if (e.getErrorStatus() == ErrorStatus.ACCESS_TOKEN_EXPIRED || e.getErrorStatus() == ErrorStatus.REFRESH_TOKEN_EXPIRED) {
                log.info("Skip invalidation: token already expired or invalidated.");
                return;
            }
            throw e;
        }
    }

    public boolean isTokenValid(String token, TokenType expectedType) {
        try {
            verifyToken(token, expectedType);
            return true;
        } catch (AppException e) {
            return false;
        }
    }
}

