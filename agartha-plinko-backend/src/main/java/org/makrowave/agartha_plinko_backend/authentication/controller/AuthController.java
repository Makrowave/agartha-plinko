package org.makrowave.agartha_plinko_backend.authentication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.authentication.domain.LoginDto;
import org.makrowave.agartha_plinko_backend.authentication.domain.RegisterDto;
import org.makrowave.agartha_plinko_backend.authentication.domain.TokenDto;
import org.makrowave.agartha_plinko_backend.authentication.service.AuthService;
import org.makrowave.agartha_plinko_backend.authentication.service.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public TokenDto login(@RequestBody LoginDto req) {
        Authentication auth = manager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        String token = jwtService.generateToken(req.username());
        return new TokenDto(token);
    }

    @PostMapping("/register")
    public TokenDto register(@Valid @RequestBody RegisterDto req) {
        var user = authService.register(req.username(), req.email(), req.password());
        String token = jwtService.generateToken(user.getUsername());
        return new TokenDto(token);
    }

    @PostMapping("/change-password/{userId}")
    public void changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        authService.changePassword(userId, oldPassword, newPassword);
    }
}
