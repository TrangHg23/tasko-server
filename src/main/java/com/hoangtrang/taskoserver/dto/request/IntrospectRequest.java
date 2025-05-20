package com.hoangtrang.taskoserver.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class IntrospectRequest {
    String accessToken;
}
