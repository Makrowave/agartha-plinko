package org.makrowave.agartha_plinko_backend.authentication.domain;

public record RegisterDto(
        String username,
        String email,
        String password
) {}
