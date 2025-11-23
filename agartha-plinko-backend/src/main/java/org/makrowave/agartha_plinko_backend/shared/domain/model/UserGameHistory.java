package org.makrowave.agartha_plinko_backend.shared.domain.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;
import org.makrowave.agartha_plinko_backend.shared.domain.GameType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameType gameType;

    private Long gameId;

    @Column(nullable = false)
    private BigDecimal betAmount;

    @Column(nullable = false)
    private BigDecimal resultAmount;

    @Column(nullable = false)
    private LocalDateTime playedAt;
}

