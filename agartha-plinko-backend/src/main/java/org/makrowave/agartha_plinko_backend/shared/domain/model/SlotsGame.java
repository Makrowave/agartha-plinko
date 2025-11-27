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
public class SlotsGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User player;

    // Stores the grid as a flat list (e.g., 3x3 grid = 9 items)
    @ElementCollection
    @CollectionTable(name = "slots_game_grid", joinColumns = @JoinColumn(name = "slots_game_id"))
    @Column(name = "symbol")
    private List<String> grid;

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