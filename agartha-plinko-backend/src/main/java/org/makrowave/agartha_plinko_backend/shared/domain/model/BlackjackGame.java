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
public class BlackjackGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User player;

    @ElementCollection
    @CollectionTable(name = "blackjack_player_cards", joinColumns = @JoinColumn(name = "blackjack_game_id"))
    @Column(name = "card")
    private List<String> playerCards;

    @ElementCollection
    @CollectionTable(name = "blackjack_dealer_cards", joinColumns = @JoinColumn(name = "blackjack_game_id"))
    @Column(name = "card")
    private List<String> dealerCards;

    @Column
    private boolean didPlayerStand = false;

    @Column(nullable = false)
    private BigDecimal betAmount;

    @Column
    private BigDecimal resultAmount;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status; // e.g., IN_PROGRESS, WON, LOST, DRAW
}

