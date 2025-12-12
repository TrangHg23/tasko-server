package com.hoangtrang.taskoserver.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
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

    Instant expiryDate;

    boolean used = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
