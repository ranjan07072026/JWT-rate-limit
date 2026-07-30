package com.example.securitypoc.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds) {}
