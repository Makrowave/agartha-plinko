package org.makrowave.agartha_plinko_backend.blackjack;

import org.makrowave.agartha_plinko_backend.shared.domain.model.BlackjackGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBlackjackGameRepository extends JpaRepository<BlackjackGame, Long> {

}
