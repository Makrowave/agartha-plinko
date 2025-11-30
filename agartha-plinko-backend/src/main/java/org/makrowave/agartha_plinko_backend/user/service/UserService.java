package org.makrowave.agartha_plinko_backend.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.shared.util.Validation;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository repo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
    }

    @Override
    @Transactional
    public void subtractUserBalance(BigDecimal value, Long userId) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var newBalance = user.getBalance().subtract(value);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        user.setBalance(newBalance);
        repo.save(user);
    }

    @Override
    @Transactional
    public void addUserBalance(BigDecimal value, Long userId) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setBalance(user.getBalance().add(value));
        repo.save(user);
    }

    @Override
    @Transactional
    public void redeemDailyBalance(Long userId, BigDecimal dailyAmount) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var today = new Date();
        if (user.getLastDailyFundsRedeemed() != null) {
            var last = user.getLastDailyFundsRedeemed();
            if (last.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    .isEqual(today.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Daily reward already redeemed");
            }
        }

        user.setBalance(user.getBalance().add(dailyAmount));
        user.setLastDailyFundsRedeemed(today);

        repo.save(user);
    }

    @Override
    @Transactional
    public void changeUsername(Long userId, String newUsername) {
        var exists = repo.findByUsername(newUsername);
        if (exists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }

        var user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setUsername(newUsername);
        repo.save(user);
    }

    @Override
    @Transactional
    public void changeEmail(Long userId, String newEmail) {
        if (!Validation.EMAIL_REGEX.matcher(newEmail).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }

        var exists = repo.findByEmail(newEmail);
        if (exists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        var user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEmail(newEmail);
        repo.save(user);
    }

}
