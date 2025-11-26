package org.makrowave.agartha_plinko_backend.roulette.domain;

import lombok.Getter;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.model.RouletteGame;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RouletteGameDto {

    private final Long gameId;
    private final Long userId;
    private final List<RouletteBetDto> bets;
    private final Integer rolledNumber;
    private final String rolledColor;
    private final BigDecimal totalBetAmount;
    private final BigDecimal winAmount;
    private final GameStatus status;
    private final LocalDateTime playedAt;

    public RouletteGameDto(RouletteGame game) {
        this.gameId = game.getId();
        this.userId = game.getPlayer().getUserId();
        this.bets = game.getBets().stream().map(RouletteBetDto::new).toList();
        this.rolledNumber = game.getRolledNumber();
        this.rolledColor = game.getRolledColor();
        this.totalBetAmount = game.getTotalBetAmount();
        this.winAmount = game.getWinAmount();
        this.status = game.getStatus();
        this.playedAt = game.getPlayedAt();
    }
}
