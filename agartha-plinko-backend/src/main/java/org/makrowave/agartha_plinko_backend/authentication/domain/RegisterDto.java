package org.makrowave.agartha_plinko_backend.authentication.domain;

import jakarta.validation.constraints.NotBlank;

public record RegisterDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password
) {
}
