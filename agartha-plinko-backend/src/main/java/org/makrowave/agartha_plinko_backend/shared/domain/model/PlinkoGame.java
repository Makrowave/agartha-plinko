package org.makrowave.agartha_plinko_backend.shared.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.makrowave.agartha_plinko_backend.plinko.domain.PlinkoRisk;
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
public class PlinkoGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User player;

    @Column(nullable = false)
    private Integer rowCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlinkoRisk riskLevel;

    @ElementCollection
    @CollectionTable(name = "plinko_game_path", joinColumns = @JoinColumn(name = "plinko_game_id"))
    @Column(name = "direction")
    private List<String> path; // "L" for Left, "R" for Right

    @Column(nullable = false)
    private Integer destinationIndex;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal multiplier;

    @Column(nullable = false)
    private BigDecimal betAmount;

    @Column
    private BigDecimal resultAmount;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;
}