package org.makrowave.agartha_plinko_backend.plinko.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.plinko.domain.PlinkoGameDto;
import org.makrowave.agartha_plinko_backend.plinko.domain.PlinkoRisk;
import org.makrowave.agartha_plinko_backend.plinko.repository.IPlinkoGameRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.model.PlinkoGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlinkoService implements IPlinkoService {

    @Autowired
    private final IPlinkoGameRepository plinkoGameRepository;

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public PlinkoGameDto play(User player, BigDecimal betAmount, Integer rows, PlinkoRisk risk) {
        if (rows < 8 || rows > 16) {
            throw new IllegalArgumentException("Rows must be between 8 and 16");
        }

        List<String> path = new ArrayList<>();
        int rightTurns = 0;

        for (int i = 0; i < rows; i++) {
            boolean isRight = random.nextBoolean();
            if (isRight) {
                path.add("R");
                rightTurns++;
            } else {
                path.add("L");
            }
        }

        int destinationIndex = rightTurns;

        BigDecimal multiplier = calculateMultiplier(rows, risk, destinationIndex);
        BigDecimal resultAmount = betAmount.multiply(multiplier);
        GameStatus status = resultAmount.compareTo(betAmount) >= 0
                ? GameStatus.WON
                : GameStatus.LOST;

        PlinkoGame game = PlinkoGame.builder()
                .player(player)
                .betAmount(betAmount)
                .rowCount(rows)
                .riskLevel(risk)
                .path(path)
                .destinationIndex(destinationIndex)
                .multiplier(multiplier)
                .resultAmount(resultAmount)
                .status(status)
                .playedAt(LocalDateTime.now())
                .build();

        plinkoGameRepository.save(game);

        return new PlinkoGameDto(game);
    }

    private BigDecimal calculateMultiplier(Integer rows, PlinkoRisk risk, int index) {
        double center = rows / 2.0;
        double distance = Math.abs(index - center);
        double maxDistance = rows / 2.0;

        // Normalize distance 0..1
        double normalizedDist = distance / maxDistance;

        double baseMult;
        switch (risk) {
            case HIGH:
                // High risk: Center is 0.2x, Edges are 29x (exponential curve)
                baseMult = 0.2 + (29.0 * Math.pow(normalizedDist, 6));
                break;
            case MEDIUM:
                // Medium risk: Center is 0.4x, Edges are 5x
                baseMult = 0.4 + (5.0 * Math.pow(normalizedDist, 3));
                break;
            case LOW:
            default:
                // Low risk: Center is 0.5x, Edges are 2x
                baseMult = 0.6 + (2.0 * Math.pow(normalizedDist, 2));
                break;
        }

        return BigDecimal.valueOf(baseMult).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}