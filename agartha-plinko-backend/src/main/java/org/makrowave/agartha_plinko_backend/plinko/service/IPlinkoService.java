package org.makrowave.agartha_plinko_backend.plinko.service;

import org.makrowave.agartha_plinko_backend.plinko.domain.PlinkoGameDto;
import org.makrowave.agartha_plinko_backend.plinko.domain.PlinkoRisk;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;

import java.math.BigDecimal;

public interface IPlinkoService {
    PlinkoGameDto play(User player, BigDecimal betAmount, Integer rows, PlinkoRisk risk);
}