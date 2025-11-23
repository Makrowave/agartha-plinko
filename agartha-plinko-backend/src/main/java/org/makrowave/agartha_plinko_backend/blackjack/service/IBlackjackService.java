package org.makrowave.agartha_plinko_backend.blackjack.service;

import org.makrowave.agartha_plinko_backend.blackjack.domain.BlackjackGameDto;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;

import java.math.BigDecimal;

public interface IBlackjackService {
     BlackjackGameDto createGame(User player, BigDecimal betAmount);
     BlackjackGameDto hit(User player, Long gameId);
     BlackjackGameDto stand(User player, Long gameId);
}
