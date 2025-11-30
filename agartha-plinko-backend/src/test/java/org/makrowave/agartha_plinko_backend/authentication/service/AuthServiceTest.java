package org.makrowave.agartha_plinko_backend.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long userId;

    @BeforeEach
    void setUp() {
        var user = userRepository.save(
                User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .hash(passwordEncoder.encode("oldpassword"))
                        .balance(BigDecimal.ZERO)
                        .build()
        );
        userId = user.getUserId();
    }

    @Test
    void givenValidData_whenRegister_thenUserCreated() {
        var user = authService.register("newuser", "new@example.com", "password123");
        assertNotNull(user.getUserId());
        assertEquals("newuser", user.getUsername());
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                authService.register("testuser", "other@example.com", "password123")
        );
        assertEquals("400 BAD_REQUEST \"Username already exists\"", ex.getMessage());
    }

    @Test
    void givenInvalidEmail_whenRegister_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                authService.register("newuser", "invalid-email", "password123")
        );
        assertEquals("400 BAD_REQUEST \"Invalid email format\"", ex.getMessage());
    }

    @Test
    void givenExistingEmail_whenRegister_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                authService.register("anotheruser", "test@example.com", "password123")
        );
        assertEquals("400 BAD_REQUEST \"Email already exists\"", ex.getMessage());
    }

    @Test
    void givenCorrectOldPassword_whenChangePassword_thenPasswordUpdated() {
        authService.changePassword(userId, "oldpassword", "newpassword");
        var user = userRepository.findById(userId).orElseThrow();
        assertTrue(passwordEncoder.matches("newpassword", user.getPassword()));
    }

    @Test
    void givenIncorrectOldPassword_whenChangePassword_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                authService.changePassword(userId, "wrongpassword", "newpassword")
        );
        assertEquals("400 BAD_REQUEST \"Invalid current password\"", ex.getMessage());
    }

    @Test
    void givenNonExistentUser_whenChangePassword_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                authService.changePassword(999L, "oldpassword", "newpassword")
        );
        assertEquals("404 NOT_FOUND \"User not found\"", ex.getMessage());
    }
}
