package org.makrowave.agartha_plinko_backend.user.repository;

import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<Object> findByEmail(String email);
}
