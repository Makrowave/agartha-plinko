package org.makrowave.agartha_plinko_backend.user.repository;

import org.makrowave.agartha_plinko_backend.shared.domain.GameType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.RouletteGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.UserGameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserGameHistoryRepository extends JpaRepository<UserGameHistory, Long> {
    Optional<UserGameHistory> findByGameIdAndGameType(Long gameId, GameType gameType);
}
