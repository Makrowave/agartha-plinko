package org.makrowave.agartha_plinko_backend.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.makrowave.agartha_plinko_backend.BaseTest;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserServiceTest extends BaseTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        var user = userRepository.save(
                org.makrowave.agartha_plinko_backend.shared.domain.model.User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .hash("dummyhash")
                        .balance(BigDecimal.valueOf(100))
                        .lastDailyFundsRedeemed(null)
                        .build()
        );
        userId = user.getUserId();
    }

    @Test
    void givenValidUser_whenRedeemDailyBalance_thenBalanceIncreased() {
        userService.redeemDailyBalance(userId, BigDecimal.valueOf(10));
        var user = userRepository.findById(userId).orElseThrow();
        assertEquals(110, user.getBalance().intValue());
    }

    @Test
    void givenAlreadyRedeemedToday_whenRedeemDailyBalance_thenThrowsException() {
        userService.redeemDailyBalance(userId, BigDecimal.valueOf(10));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.redeemDailyBalance(userId, BigDecimal.valueOf(10))
        );

        assertEquals("400 BAD_REQUEST \"Daily reward already redeemed\"", ex.getMessage());
    }

    @Test
    void givenValidUser_whenSubtractBalance_thenBalanceDecreased() {
        userService.subtractUserBalance(BigDecimal.valueOf(30), userId);
        var user = userRepository.findById(userId).orElseThrow();
        assertEquals(70, user.getBalance().intValue());
    }

    @Test
    void givenInsufficientBalance_whenSubtractBalance_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.subtractUserBalance(BigDecimal.valueOf(200), userId)
        );
        assertEquals("400 BAD_REQUEST \"Insufficient balance\"", ex.getMessage());
    }

    @Test
    void givenValidUser_whenAddBalance_thenBalanceIncreased() {
        userService.addUserBalance(BigDecimal.valueOf(50), userId);
        var user = userRepository.findById(userId).orElseThrow();
        assertEquals(150, user.getBalance().intValue());
    }

    @Test
    void givenNewUsername_whenChangeUsername_thenUsernameUpdated() {
        userService.changeUsername(userId, "newUsername");
        var user = userRepository.findById(userId).orElseThrow();
        assertEquals("newUsername", user.getUsername());
    }

    @Test
    void givenTakenUsername_whenChangeUsername_thenThrowsException() {
        userRepository.save(
                org.makrowave.agartha_plinko_backend.shared.domain.model.User.builder()
                        .username("taken")
                        .email("other@example.com")
                        .hash("hash")
                        .balance(BigDecimal.ZERO)
                        .build()
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.changeUsername(userId, "taken")
        );
        assertEquals("400 BAD_REQUEST \"Username already taken\"", ex.getMessage());
    }

    @Test
    void givenValidEmail_whenChangeEmail_thenEmailUpdated() {
        userService.changeEmail(userId, "newemail@example.com");
        var user = userRepository.findById(userId).orElseThrow();
        assertEquals("newemail@example.com", user.getEmail());
    }

    @Test
    void givenInvalidEmail_whenChangeEmail_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.changeEmail(userId, "invalid-email")
        );
        assertEquals("400 BAD_REQUEST \"Invalid email\"", ex.getMessage());
    }

    @Test
    void givenTakenEmail_whenChangeEmail_thenThrowsException() {
        userRepository.save(
                org.makrowave.agartha_plinko_backend.shared.domain.model.User.builder()
                        .username("otheruser")
                        .email("taken@example.com")
                        .hash("hash")
                        .balance(BigDecimal.ZERO)
                        .build()
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.changeEmail(userId, "taken@example.com")
        );
        assertEquals("400 BAD_REQUEST \"Email already in use\"", ex.getMessage());
    }
}
