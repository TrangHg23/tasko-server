package com.hoangtrang.taskoserver.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshResponse {
    boolean authenticated;
    String accessToken;
    String tokenType;
    long expiresAt;
}
