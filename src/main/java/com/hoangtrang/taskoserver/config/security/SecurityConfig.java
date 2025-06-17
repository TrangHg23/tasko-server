package com.hoangtrang.taskoserver.config.security;

import com.hoangtrang.taskoserver.config.converter.CustomJwtAuthConverter;
import com.hoangtrang.taskoserver.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final CustomJwtAuthConverter customJwtAuthConverter;

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    private final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/sign-up",
            "/api/auth/log-in",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        return new NimbusJwtEncoder(new ImmutableSecret<>(keySpec));
    }

    @Bean
    public JwtBlacklistFilter jwtBlacklistFilter() {
        return new JwtBlacklistFilter(invalidatedTokenRepository, jwtDecoder(), new JwtAuthEntryPoint());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(customJwtAuthConverter))
                        .authenticationEntryPoint(new JwtAuthEntryPoint()));

        http.addFilterBefore(jwtBlacklistFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
