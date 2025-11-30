package org.makrowave.agartha_plinko_backend.wallet.domain;

import lombok.Builder;
import lombok.Data;
import org.makrowave.agartha_plinko_backend.shared.domain.GameType;
import org.makrowave.agartha_plinko_backend.shared.domain.TransactionType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.WalletTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionDto {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private GameType gameType;
    private Long gameId;
    private LocalDateTime createdAt;

    public static WalletTransactionDto fromEntity(WalletTransaction entity) {
        return WalletTransactionDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .balanceAfter(entity.getBalanceAfter())
                .gameType(entity.getGameType())
                .gameId(entity.getGameId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}