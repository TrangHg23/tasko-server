package com.hoangtrang.taskoserver.config;

import com.hoangtrang.taskoserver.dto.request.IntrospectRequest;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${spring.security.jwt.secret-key}")
    private String signerKey;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws BadJwtException {

        try {
            var response = authServiceImpl.introspect(IntrospectRequest.builder()
                    .accessToken(token)
                    .build());

            if (!response.isValid())
                throw new BadJwtException("Invalid token");
        } catch (AppException e) {
            throw new BadJwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
