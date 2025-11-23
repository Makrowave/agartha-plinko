package org.makrowave.agartha_plinko_backend.blackjack.domain;

import lombok.Getter;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.model.BlackjackGame;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BlackjackGameDto {

    private final Long gameId;
    private final Long userId;
    private final List<String> playerCards;
    private final List<String> dealerCards;
    private final boolean didPlayerStand;
    private final BigDecimal betAmount;
    private final BigDecimal resultAmount;
    private final LocalDateTime playedAt;
    private final GameStatus status;

    public BlackjackGameDto(BlackjackGame game) {
        this.gameId = game.getId();
        this.userId = game.getPlayer().getUserId();
        this.playerCards = game.getPlayerCards();
        this.didPlayerStand = game.isDidPlayerStand();
        this.betAmount = game.getBetAmount();
        this.resultAmount = game.getResultAmount();
        this.playedAt = game.getPlayedAt();
        this.status = game.getStatus();

        if (status == GameStatus.IN_PROGRESS && !didPlayerStand) {
            this.dealerCards = List.of(game.getDealerCards().get(0));
        } else {
            this.dealerCards = game.getDealerCards();
        }
    }
}
