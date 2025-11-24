package org.makrowave.agartha_plinko_backend.user.service;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) {
       return repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Not found"));
    }
}
