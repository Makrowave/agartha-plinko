package org.makrowave.agartha_plinko_backend.authentication.service;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private static final int DEFAULT_BALANCE = 100;

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .hash(passwordEncoder.encode(rawPassword))
                .balance(BigDecimal.valueOf(DEFAULT_BALANCE))
                .build();

        return userRepository.save(user);
    }
}
