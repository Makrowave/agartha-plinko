package org.makrowave.agartha_plinko_backend.authentication.service;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.shared.util.Validation;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (!Validation.EMAIL_REGEX.matcher(email).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .hash(passwordEncoder.encode(rawPassword))
                .balance(BigDecimal.ZERO)
                .build();

        return userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid current password");
        }

        user.setHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
