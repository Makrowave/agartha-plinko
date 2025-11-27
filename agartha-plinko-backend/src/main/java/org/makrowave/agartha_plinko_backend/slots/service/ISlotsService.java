package org.makrowave.agartha_plinko_backend.slots.service;

import org.makrowave.agartha_plinko_backend.slots.domain.SlotsGameDto;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;

import java.math.BigDecimal;

public interface ISlotsService {
    SlotsGameDto spin(User player, BigDecimal betAmount);
}