package com.hoangtrang.taskoserver.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfo {
    UUID id;
    String email;
    String name;
}
