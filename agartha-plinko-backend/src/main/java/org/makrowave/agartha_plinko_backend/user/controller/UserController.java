package org.makrowave.agartha_plinko_backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.user.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;
    private final BigDecimal dailyAmount = new BigDecimal("5.0");

    @PostMapping("/{userId}/redeem-daily")
    public ResponseEntity<Void> redeemDaily(@PathVariable Long userId) {
        userService.redeemDailyBalance(userId, dailyAmount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/change-username")
    public ResponseEntity<Void> changeUsername(
            @PathVariable Long userId,
            @RequestParam String username
    ) {
        userService.changeUsername(userId, username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/change-email")
    public ResponseEntity<Void> changeEmail(
            @PathVariable Long userId,
            @RequestParam String email
    ) {
        userService.changeEmail(userId, email);
        return ResponseEntity.ok().build();
    }
}
