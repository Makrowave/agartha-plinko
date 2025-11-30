package org.makrowave.agartha_plinko_backend.plinko.domain;

import lombok.Getter;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.PlinkoDirection;
import org.makrowave.agartha_plinko_backend.shared.domain.model.PlinkoGame;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PlinkoGameDto {

    private final Long gameId;
    private final Long userId;
    private final Integer rowCount;
    private final PlinkoRisk riskLevel;
    private final List<PlinkoDirection> path;
    private final Integer destinationIndex;
    private final BigDecimal multiplier;
    private final BigDecimal betAmount;
    private final BigDecimal resultAmount;
    private final LocalDateTime playedAt;
    private final GameStatus status;

    public PlinkoGameDto(PlinkoGame game) {
        this.gameId = game.getId();
        this.userId = game.getPlayer().getUserId();
        this.rowCount = game.getRowCount();
        this.riskLevel = game.getRiskLevel();
        this.path = game.getPath();
        this.destinationIndex = game.getDestinationIndex();
        this.multiplier = game.getMultiplier();
        this.betAmount = game.getBetAmount();
        this.resultAmount = game.getResultAmount();
        this.playedAt = game.getPlayedAt();
        this.status = game.getStatus();
    }
}