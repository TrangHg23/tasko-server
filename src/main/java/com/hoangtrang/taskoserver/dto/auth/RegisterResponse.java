package com.hoangtrang.taskoserver.dto.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterResponse {
    UUID id;
    String email;
    String name;
    OffsetDateTime createdAt;
}
