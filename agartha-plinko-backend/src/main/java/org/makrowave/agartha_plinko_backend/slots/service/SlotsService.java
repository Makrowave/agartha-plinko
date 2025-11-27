package org.makrowave.agartha_plinko_backend.slots.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.model.SlotsGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.slots.domain.SlotSymbol;
import org.makrowave.agartha_plinko_backend.slots.domain.SlotsGameDto;
import org.makrowave.agartha_plinko_backend.slots.repository.ISlotsGameRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SlotsService implements ISlotsService {

    private final ISlotsGameRepository slotsGameRepository;
    private final Random random = new Random();

    private final int COLS = 3;
    private final int ROWS = 3;

    @Override
    @Transactional
    public SlotsGameDto spin(User player, BigDecimal betAmount) {
        List<SlotSymbol> symbolGrid = generateRandomGrid();
        List<String> gridStrings = symbolGrid.stream().map(Enum::toString).toList();

        BigDecimal payoutMultiplier = calculatePayoutMultiplier(symbolGrid);
        BigDecimal winAmount = betAmount.multiply(payoutMultiplier);

        GameStatus status = winAmount.compareTo(BigDecimal.ZERO) > 0 ? GameStatus.WON : GameStatus.LOST;

        SlotsGame game = SlotsGame.builder()
                .player(player)
                .betAmount(betAmount)
                .grid(gridStrings)
                .resultAmount(winAmount)
                .status(status)
                .playedAt(LocalDateTime.now())
                .build();

        slotsGameRepository.save(game);

        return new SlotsGameDto(game);
    }

    private List<SlotSymbol> generateRandomGrid() {
        List<SlotSymbol> grid = new ArrayList<>();
        int totalCells = COLS * ROWS;
        SlotSymbol[] symbols = SlotSymbol.values();

        for (int i = 0; i < totalCells; i++) {
            grid.add(symbols[random.nextInt(symbols.length)]);
        }
        return grid;
    }

    private BigDecimal calculatePayoutMultiplier(List<SlotSymbol> grid) {
        double totalMultiplier = 0.0;

        for (int row = 0; row < ROWS; row++) {
            if (checkLine(grid, row * COLS, 1)) {
                totalMultiplier += grid.get(row * COLS).getMultiplier();
            }
        }

        if (COLS == 3 && ROWS == 3) {
            if (checkLine(grid, 0, COLS + 1)) {
                totalMultiplier += grid.get(0).getMultiplier();
            }
            if (checkLine(grid, COLS * (ROWS - 1), -(COLS - 1))) {
                totalMultiplier += grid.get(COLS * (ROWS - 1)).getMultiplier();
            }
        }

        return BigDecimal.valueOf(totalMultiplier);
    }

    /**
     * Checks if a line consists of identical symbols.
     * @param grid The flattened grid.
     * @param startIndex The index to start checking.
     * @param step The step to get to the next cell in the line.
     */
    private boolean checkLine(List<SlotSymbol> grid, int startIndex, int step) {
        SlotSymbol first = grid.get(startIndex);
        for (int i = 1; i < COLS; i++) {
            if (grid.get(startIndex + (i * step)) != first) {
                return false;
            }
        }
        return true;
    }
}