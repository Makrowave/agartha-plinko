package org.makrowave.agartha_plinko_backend.shared.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.makrowave.agartha_plinko_backend.shared.domain.GameType;
import org.makrowave.agartha_plinko_backend.shared.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount; // Negative for BET, Positive for WIN

    @Column(nullable = false)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    private GameType gameType; // Nullable (if deposit/withdraw)
    private Long gameId;       // Nullable

    @Column(nullable = false)
    private LocalDateTime createdAt;
}