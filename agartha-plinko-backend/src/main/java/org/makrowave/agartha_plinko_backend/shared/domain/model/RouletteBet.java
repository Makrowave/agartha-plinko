package org.makrowave.agartha_plinko_backend.shared.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.makrowave.agartha_plinko_backend.shared.domain.RouletteBetType;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouletteBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roulette_game_id")
    private RouletteGame game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouletteBetType betType;

    @Column
    private Integer number;

    @Column(nullable = false)
    private BigDecimal betAmount;

    @Column
    private BigDecimal wonAmount;
}
