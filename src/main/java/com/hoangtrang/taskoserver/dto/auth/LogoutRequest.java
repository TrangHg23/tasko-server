package com.hoangtrang.taskoserver.dto.auth;

public record LogoutRequest(String accessToken, String refreshToken){}
