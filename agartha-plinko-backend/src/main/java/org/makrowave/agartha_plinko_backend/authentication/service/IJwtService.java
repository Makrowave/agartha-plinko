package org.makrowave.agartha_plinko_backend.authentication.service;

public interface IJwtService {
    String generateToken(String username);
    String extractUsername(String token);
    boolean validate(String token);
}
