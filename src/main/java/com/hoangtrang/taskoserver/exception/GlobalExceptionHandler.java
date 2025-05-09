package com.hoangtrang.taskoserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleRunTimeException(RuntimeException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(ErrorStatus.UNCATEGORIZED_EXCEPTION.getStatus());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(ErrorStatus.UNCATEGORIZED_EXCEPTION.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ErrorResponse> handleRunTimeException(AppException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        ErrorStatus errorStatus = e.getErrorStatus();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(errorStatus.getStatus());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(errorStatus.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        ErrorStatus errorStatus = ErrorStatus.INVALID_KEY;
        FieldError fieldError = e.getFieldError();

        if (fieldError != null) {
            String enumKey = fieldError.getDefaultMessage();
            if (enumKey != null) {
                try {
                    errorStatus = ErrorStatus.valueOf(enumKey);
                } catch (IllegalArgumentException ignored) {

                }
            }
        }
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(errorStatus.getStatus());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(errorStatus.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}