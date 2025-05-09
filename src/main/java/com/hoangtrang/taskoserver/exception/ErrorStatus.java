package com.hoangtrang.taskoserver.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    INVALID_KEY(1001, "Uncategorized error" ),
    EMAIL_REQUIRED(1002, "Email is required"),
    EMAIL_INVALID(1003, "Email is invalid"),
    EMAIL_EXISTED(1004, "Email already existed"),
    PASSWORD_REQUIRED(1005, "Password is required"),
    PASSWORD_INVALID(1006, "Password must be at least 8 characters")
    ;
    private final int status;
    private final String message;

}
