package org.makrowave.agartha_plinko_backend.authentication.controller;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.authentication.domain.LoginDto;
import org.makrowave.agartha_plinko_backend.authentication.domain.TokenDto;
import org.makrowave.agartha_plinko_backend.authentication.service.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final JwtService jwtService;


    @PostMapping("/login")
    public TokenDto login(@RequestBody LoginDto req) {
        Authentication auth = manager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        String token = jwtService.generateToken(req.username());
        return new TokenDto(token);
    }
}
