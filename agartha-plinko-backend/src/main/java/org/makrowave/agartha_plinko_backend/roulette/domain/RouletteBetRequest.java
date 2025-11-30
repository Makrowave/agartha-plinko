package org.makrowave.agartha_plinko_backend.roulette.domain;

import lombok.Data;
import org.makrowave.agartha_plinko_backend.shared.domain.RouletteBetType;

import java.math.BigDecimal;

@Data
public class RouletteBetRequest {

    private RouletteBetType betType;

    private Integer number;

    private BigDecimal betAmount;
}
