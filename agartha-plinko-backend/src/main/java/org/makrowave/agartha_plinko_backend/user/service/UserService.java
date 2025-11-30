package org.makrowave.agartha_plinko_backend.user.service;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
    }

    @Override
    public void subtractUserBalance(BigDecimal value, Long userId) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var newBalance = user.getBalance().subtract(value);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.setBalance(newBalance);
        repo.save(user);
    }

    @Override
    public void addUserBalance(BigDecimal value, Long userId) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setBalance(user.getBalance().add(value));
        repo.save(user);
    }
}
