package org.makrowave.agartha_plinko_backend.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "01234567890123456789012345678901";

    @BeforeEach
    void setUp() {
        long expirationMs = 3600000;
        jwtService = new JwtService(secret, expirationMs);
    }

    @Test
    void givenUsername_whenGenerateToken_thenTokenNotNull() {
        String token = jwtService.generateToken("user1");
        assertNotNull(token);
    }

    @Test
    void givenValidToken_whenExtractUsername_thenCorrectUsername() {
        String token = jwtService.generateToken("user1");
        String username = jwtService.extractUsername(token);
        assertEquals("user1", username);
    }

    @Test
    void givenValidTokenAndOtherUsername_whenExtractUsername_thenIncorrectUsername() {
        String token = jwtService.generateToken("user2");
        String username = jwtService.extractUsername(token);
        assertNotEquals("user1", username);
    }

    @Test
    void givenValidToken_whenValidate_thenReturnsTrue() {
        String token = jwtService.generateToken("user1");
        assertTrue(jwtService.validate(token));
    }

    @Test
    void givenInvalidToken_whenValidate_thenReturnsFalse() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtService.validate(invalidToken));
    }

    @Test
    void givenExpiredToken_whenValidate_thenReturnsFalse() throws InterruptedException {
        JwtService shortExpiryService = new JwtService(secret, 10); // 10ms
        String token = shortExpiryService.generateToken("user1");
        Thread.sleep(20);
        assertFalse(shortExpiryService.validate(token));
    }
}
