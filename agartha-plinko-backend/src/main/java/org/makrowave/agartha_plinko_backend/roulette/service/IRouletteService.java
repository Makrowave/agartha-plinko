package org.makrowave.agartha_plinko_backend.roulette.service;

import org.makrowave.agartha_plinko_backend.roulette.domain.RouletteBetRequest;
import org.makrowave.agartha_plinko_backend.roulette.domain.RouletteGameDto;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;

public interface IRouletteService {

    RouletteGameDto placeBet(User player, RouletteBetRequest bet);

    RouletteGameDto spin(User player, Long gameId);

    RouletteGameDto getGame(Long gameId, User player);
}
