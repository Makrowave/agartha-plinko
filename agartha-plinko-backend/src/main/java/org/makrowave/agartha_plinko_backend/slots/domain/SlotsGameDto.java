package org.makrowave.agartha_plinko_backend.slots.domain;

import lombok.Getter;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.model.SlotsGame;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SlotsGameDto {

    private final Long gameId;
    private final Long userId;
    private final List<String> grid; // Represents a 3x3 matrix flattened
    private final BigDecimal betAmount;
    private final BigDecimal resultAmount;
    private final LocalDateTime playedAt;
    private final GameStatus status;

    public SlotsGameDto(SlotsGame game) {
        this.gameId = game.getId();
        this.userId = game.getPlayer().getUserId();
        this.grid = game.getGrid();
        this.betAmount = game.getBetAmount();
        this.resultAmount = game.getResultAmount();
        this.playedAt = game.getPlayedAt();
        this.status = game.getStatus();
    }
}