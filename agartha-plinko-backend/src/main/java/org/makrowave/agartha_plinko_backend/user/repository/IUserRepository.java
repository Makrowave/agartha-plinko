package org.makrowave.agartha_plinko_backend.user.repository;

import jakarta.persistence.LockModeType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<Object> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByIdWithLock(@Param("userId") Long userId);
}
