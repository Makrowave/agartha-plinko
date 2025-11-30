package org.makrowave.agartha_plinko_backend.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.math.BigDecimal;

public interface IUserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);

    void subtractUserBalance(BigDecimal value, Long userId);

    void addUserBalance(BigDecimal value, Long userId);

    void redeemDailyBalance(Long userId, BigDecimal dailyAmount);

    public void changeUsername(Long userId, String newUsername);

    public void changeEmail(Long userId, String newEmail);
}
