package com.hoangtrang.taskoserver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST ),
    EMAIL_REQUIRED(1002, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1003, "Email is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1004, "Email already existed", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1005, "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1006, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1007, "User doesn't exist", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1008, "Category already existed", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(1009, "Category doesn't exist", HttpStatus.NOT_FOUND),
    NAME_REQUIRED(1010, "Name is required", HttpStatus.BAD_REQUEST),
    TITLE_REQUIRED(1011, "Title is required", HttpStatus.BAD_REQUEST),
    TASK_NOT_FOUND(1012, "Task is required", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(1013, "Reset token doesn't exist", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED(1014, "Reset token expired", HttpStatus.BAD_REQUEST),
    TOKEN_ALREADY_USED(1015, "Reset token already used", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED(1101, "You do not have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1100, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED(1102, "Access token expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1103, "Refresh token expired, please login again", HttpStatus.UNAUTHORIZED),
    ;
    private final int status;
    private final String message;
    private final HttpStatus statusCode;

}
