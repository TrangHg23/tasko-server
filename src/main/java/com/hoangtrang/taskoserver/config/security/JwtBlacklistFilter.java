package com.hoangtrang.taskoserver.config.security;

import com.hoangtrang.taskoserver.repository.InvalidatedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtBlacklistFilter extends OncePerRequestFilter {
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final JwtDecoder jwtDecoder;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String jti = jwt.getClaim("jti");

            if (invalidatedTokenRepository.existsById(jti)) {
                jwtAuthEntryPoint.commence(
                        request,
                        response,
                        new BadCredentialsException("Token revoked"));
                return;
            }

        } catch (JwtException e) {
            jwtAuthEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid JWT", e));
            return;
        }

        filterChain.doFilter(request, response);
    }
}