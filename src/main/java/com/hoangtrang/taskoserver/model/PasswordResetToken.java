package com.hoangtrang.taskoserver.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue
    UUID id;

    String token;

    OffsetDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user;
}
