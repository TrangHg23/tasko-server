package com.hoangtrang.taskoserver.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class RefreshRequest {
    String refreshToken;
}
