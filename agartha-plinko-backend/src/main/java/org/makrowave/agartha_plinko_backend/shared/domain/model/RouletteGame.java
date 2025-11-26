package org.makrowave.agartha_plinko_backend.shared.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouletteGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User player;

    @Column(nullable = false)
    private BigDecimal totalBetAmount;

    @Column
    private BigDecimal winAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    // Rolled number from 0 to 36 (null before spin)
    @Column
    private Integer rolledNumber;

    // "RED", "BLACK", "GREEN"
    @Column
    private String rolledColor;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouletteBet> bets;
}
