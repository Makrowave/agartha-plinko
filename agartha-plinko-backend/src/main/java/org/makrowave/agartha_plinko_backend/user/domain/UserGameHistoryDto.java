package org.makrowave.agartha_plinko_backend.user.domain;

import lombok.Builder;
import lombok.Data;
import org.makrowave.agartha_plinko_backend.shared.domain.GameType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.UserGameHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserGameHistoryDto {
    private Long id;
    private GameType gameType;
    private Long gameId;
    private BigDecimal betAmount;
    private BigDecimal resultAmount;
    private LocalDateTime playedAt;

    public static UserGameHistoryDto fromEntity(UserGameHistory entity) {
        return UserGameHistoryDto.builder()
                .id(entity.getId())
                .gameType(entity.getGameType())
                .gameId(entity.getGameId())
                .betAmount(entity.getBetAmount())
                .resultAmount(entity.getResultAmount())
                .playedAt(entity.getPlayedAt())
                .build();
    }
}