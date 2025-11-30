package org.makrowave.agartha_plinko_backend.roulette.domain;

import lombok.Getter;
import org.makrowave.agartha_plinko_backend.shared.domain.RouletteBetType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.RouletteBet;

import java.math.BigDecimal;

@Getter
public class RouletteBetDto {

    private final Long betId;
    private final RouletteBetType betType;
    private final Integer number;
    private final BigDecimal betAmount;
    private final BigDecimal wonAmount;

    public RouletteBetDto(RouletteBet bet) {
        this.betId = bet.getId();
        this.betType = bet.getBetType();
        this.number = bet.getNumber();
        this.betAmount = bet.getBetAmount();
        this.wonAmount = bet.getWonAmount();
    }
}
