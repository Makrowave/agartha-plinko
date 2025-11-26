package org.makrowave.agartha_plinko_backend.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);
}
