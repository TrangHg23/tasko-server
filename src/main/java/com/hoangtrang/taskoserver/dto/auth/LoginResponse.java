package com.hoangtrang.taskoserver.dto.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    boolean authenticated;
    String accessToken;
    String refreshToken;
    String tokenType;
    long expiresAt;
    UserInfo user;
}
