package com.hoangtrang.taskoserver.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST ),
    EMAIL_REQUIRED(1002, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1003, "Email is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1004, "Email already existed", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1005, "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1006, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1007, "User doesn't exist", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1008, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1009, "You do not have permission", HttpStatus.FORBIDDEN)
    ;
    private final int status;
    private final String message;
    private final HttpStatusCode statusCode;

}
