package org.makrowave.agartha_plinko_backend.authentication.service;

import org.makrowave.agartha_plinko_backend.shared.domain.model.User;

public interface IAuthService {
    User register(String username, String email, String rawPassword);

    public void changePassword(Long userId, String oldPassword, String newPassword);
}
