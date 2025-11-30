package org.makrowave.agartha_plinko_backend.wallet.service;

import org.makrowave.agartha_plinko_backend.shared.domain.GameType;

import java.math.BigDecimal;

public interface IWalletService {
    void deductBet(Long userId, BigDecimal amount, GameType gameType, Long gameId);

    void settleBet(Long userId, BigDecimal winAmount, GameType gameType, Long gameId);
}