package org.makrowave.agartha_plinko_backend.slots.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.GameType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.SlotsGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.slots.domain.SlotSymbol;
import org.makrowave.agartha_plinko_backend.slots.domain.SlotsGameDto;
import org.makrowave.agartha_plinko_backend.slots.repository.ISlotsGameRepository;
import org.makrowave.agartha_plinko_backend.wallet.service.IWalletService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotsServiceTest {

    @Mock
    private ISlotsGameRepository slotsGameRepository;

    @Mock
    private IWalletService walletService;

    @Spy
    @InjectMocks
    private SlotsService slotsService;

    @Mock
    private Random mockedRandom;

    private User testUser;

    private final Long gameId = 1001L;
    private final BigDecimal BET_AMOUNT = new BigDecimal("5.0");
    private final SlotSymbol SYMBOL_CHERRY = SlotSymbol.CHERRY;
    private final SlotSymbol SYMBOL_SEVEN = SlotSymbol.SEVEN;

    private final int CHERRY_INDEX = 0;
    private final int SEVEN_INDEX = 6;
    private final int LEMON_INDEX = 1;

    @BeforeEach
    void setUp() throws Exception {
        testUser = User.builder()
                .userId(100L)
                .username("TestPlayer")
                .build();

        Field randomField = SlotsService.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(slotsService, mockedRandom);

        when(slotsGameRepository.save(any(SlotsGame.class))).thenAnswer(invocation -> {
            SlotsGame game = invocation.getArgument(0, SlotsGame.class);
            game.setId(gameId);
            return game;
        });
    }

    @Test
    void spin_shouldReturnCorrectWinAmount_whenFiveLinesMatch_withCherrySymbol() {
        BigDecimal expectedMultiplier = BigDecimal.valueOf(SYMBOL_CHERRY.getMultiplier())
                .multiply(new BigDecimal("5"));
        BigDecimal expectedWinAmount = BET_AMOUNT.multiply(expectedMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        when(mockedRandom.nextInt(anyInt())).thenReturn(CHERRY_INDEX);

        SlotsGameDto result = slotsService.spin(testUser, BET_AMOUNT);

        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(expectedWinAmount, result.getResultAmount());
    }

    @Test
    void spin_shouldReturnCorrectWinAmount_whenOnlyOneRowMatches_withSevenSymbol() {
        BigDecimal expectedMultiplier = BigDecimal.valueOf(SYMBOL_SEVEN.getMultiplier());
        BigDecimal expectedWinAmount = BET_AMOUNT.multiply(expectedMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        when(mockedRandom.nextInt(anyInt()))
                .thenReturn(SEVEN_INDEX, SEVEN_INDEX, SEVEN_INDEX) // Row 1 (Win)
                .thenReturn(CHERRY_INDEX, LEMON_INDEX, LEMON_INDEX) // Row 2 (No Win)
                .thenReturn(CHERRY_INDEX, LEMON_INDEX, LEMON_INDEX); // Row 3 (No Win)

        SlotsGameDto result = slotsService.spin(testUser, BET_AMOUNT);

        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(expectedWinAmount, result.getResultAmount());
    }

    @Test
    void spin_shouldReturnZeroWinAmount_whenNoLinesMatch() {
        when(mockedRandom.nextInt(anyInt()))
                .thenReturn(SEVEN_INDEX, CHERRY_INDEX, LEMON_INDEX) // Row 1
                .thenReturn(CHERRY_INDEX, SEVEN_INDEX, CHERRY_INDEX) // Row 2
                .thenReturn(LEMON_INDEX, CHERRY_INDEX, CHERRY_INDEX); // Row 3

        BigDecimal expectedWinAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        SlotsGameDto result = slotsService.spin(testUser, BET_AMOUNT);
        assertEquals(GameStatus.LOST, result.getStatus());
        assertEquals(expectedWinAmount, result.getResultAmount().setScale(2, RoundingMode.HALF_UP));
    }


    @Test
    void spin_shouldSaveGameAndSettleWallet() {
        when(mockedRandom.nextInt(anyInt())).thenReturn(CHERRY_INDEX);

        SlotsGameDto result = slotsService.spin(testUser, BET_AMOUNT);

        ArgumentCaptor<SlotsGame> gameCaptor = ArgumentCaptor.forClass(SlotsGame.class);
        verify(slotsGameRepository).save(gameCaptor.capture());

        verify(walletService).deductBet(
                eq(testUser.getUserId()),
                eq(BET_AMOUNT),
                eq(GameType.SLOTS),
                eq(gameId)
        );

        verify(walletService).settleBet(
                eq(testUser.getUserId()),
                eq(result.getResultAmount()),
                eq(GameType.SLOTS),
                eq(gameId)
        );

        SlotsGame savedGame = gameCaptor.getValue();
        assertEquals(testUser, savedGame.getPlayer());
        assertEquals(BET_AMOUNT, savedGame.getBetAmount());
        assertEquals(GameStatus.WON, savedGame.getStatus());

        String expectedSymbolString = SlotSymbol.values()[CHERRY_INDEX].toString();
        assertEquals(9, savedGame.getGrid().size());
        assertTrue(savedGame.getGrid().stream().allMatch(s -> s.equals(expectedSymbolString)));
    }
}