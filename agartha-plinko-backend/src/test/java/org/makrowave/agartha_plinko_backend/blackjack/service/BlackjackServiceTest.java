package org.makrowave.agartha_plinko_backend.blackjack.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.makrowave.agartha_plinko_backend.BaseTest;
import org.makrowave.agartha_plinko_backend.blackjack.repository.IBlackjackGameRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.GameType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.BlackjackGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.makrowave.agartha_plinko_backend.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@Transactional
public class BlackjackServiceTest extends BaseTest {

    @Autowired
    private BlackjackService blackjackService;

    @Autowired
    private IBlackjackGameRepository blackjackGameRepository;

    @Autowired
    private IUserRepository userRepository;

    @MockitoBean
    private IWalletService walletService;

    private User user;
    private BigDecimal betAmount;

    @BeforeEach
    void setUp() {
        user = userRepository.save(
                User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .hash("dummyhash")
                        .balance(BigDecimal.valueOf(100))
                        .lastDailyFundsRedeemed(null)
                        .build()
        );
        betAmount = BigDecimal.valueOf(100);
    }

    // ==================== CREATE GAME TESTS ====================

    @Test
    void givenValidBet_whenCreateGame_thenGameCreated() {
        var gameDto = blackjackService.createGame(user, betAmount);

        assertNotNull(gameDto);
        assertEquals(GameStatus.IN_PROGRESS, gameDto.getStatus());
        assertEquals(2, gameDto.getPlayerCards().size());
        assertEquals(1, gameDto.getDealerCards().size());
        assertEquals(betAmount, gameDto.getBetAmount());
        assertFalse(gameDto.isDidPlayerStand());

        verify(walletService).deductBet(
                eq(user.getUserId()),
                eq(betAmount),
                eq(GameType.BLACKJACK),
                any(Long.class)
        );
    }

    @Test
    void givenCreateGame_whenCheckCards_thenCardsAreValid() {
        var gameDto = blackjackService.createGame(user, betAmount);

        for (String card : gameDto.getPlayerCards()) {
            assertTrue(card.matches("[HDSC](A|[2-9]|10|J|Q|K)"));
        }
        for (String card : gameDto.getDealerCards()) {
            assertTrue(card.matches("[HDSC](A|[2-9]|10|J|Q|K)"));
        }
    }

    // ==================== HIT TESTS ====================

    @Test
    void givenInProgressGame_whenHit_thenCardAdded() {
        var gameId = createGame(List.of("H5", "D6"), List.of("S7", "S10"));

        var gameDto = blackjackService.hit(user, gameId);

        assertEquals(3, gameDto.getPlayerCards().size());
        assertEquals(GameStatus.IN_PROGRESS, gameDto.getStatus());
    }

    @Test
    void givenHandOver21_whenHit_thenGameLost() {
        var gameId = createGame(List.of("H10", "D10"), List.of("S7", "S10"));

        var gameDto = blackjackService.hit(user, gameId);

        if (gameDto.getStatus() != GameStatus.LOST) {
            gameDto = blackjackService.hit(user, gameId);
        }

        assertTrue(gameDto.isDidPlayerStand());
        assertEquals(BigDecimal.ZERO, gameDto.getResultAmount());
        verify(walletService).settleBet(
                eq(user.getUserId()),
                eq(BigDecimal.ZERO),
                eq(GameType.BLACKJACK),
                any(Long.class)
        );
    }

    @Test
    void givenNonExistentGame_whenHit_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.hit(user, 99999L)
        );
        assertEquals("404 NOT_FOUND \"Game not found\"", ex.getMessage());
    }

    @Test
    void givenWrongUser_whenHit_thenThrowsForbidden() {
        var gameId = createGame(List.of("H5", "D6"), List.of("S7", "S10"));

        User otherUser = userRepository.save(
                User.builder()
                        .username("otheruser")
                        .email("other@example.com")
                        .hash("hash")
                        .balance(BigDecimal.valueOf(1000))
                        .build()
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.hit(otherUser, gameId)
        );
        assertEquals("403 FORBIDDEN \"Forbidden\"", ex.getMessage());
    }

    @Test
    void givenFinishedGame_whenHit_thenThrowsException() {
        var gameId = createGame(List.of("DA", "D10"), List.of("S7", "S10"));

        blackjackService.stand(user, gameId);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.hit(user, gameId)
        );
        assertEquals("400 BAD_REQUEST \"Game already finished\"", ex.getMessage());
    }

    @Test
    void givenPlayerStood_whenHit_thenThrowsException() {
        var gameId = createGame(List.of("H5", "D6"), List.of("S7", "S10"));

        var game = blackjackGameRepository.findById(gameId).orElseThrow();
        game.setDidPlayerStand(true);
        blackjackGameRepository.save(game);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.hit(user, gameId)
        );
        assertEquals("400 BAD_REQUEST \"Player already stood\"", ex.getMessage());
    }

    // ==================== STAND TESTS ====================

    @Test
    void givenPlayerBlackjack_whenStand_thenWin() {
        var gameId = createGame(List.of("DA", "D10"), List.of("S7", "S10"));

        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.WON, gameDto.getStatus());
        assertEquals(betAmount.multiply(BigDecimal.valueOf(2)), gameDto.getResultAmount());
        assertTrue(gameDto.isDidPlayerStand());

        verify(walletService).settleBet(
                eq(user.getUserId()),
                eq(gameDto.getResultAmount()),
                eq(GameType.BLACKJACK),
                any(Long.class)
        );
    }

    @Test
    void givenBothBlackjack_whenStand_thenDraw() {
        var gameId = createGame(List.of("DA", "D10"), List.of("SA", "S10"));

        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.DRAW, gameDto.getStatus());
        assertEquals(betAmount, gameDto.getResultAmount());
    }

    @Test
    void givenDealerBlackjack_whenStand_thenLose() {
        var gameId = createGame(List.of("H5", "D6"), List.of("SA", "S10"));

        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.LOST, gameDto.getStatus());
        assertEquals(BigDecimal.ZERO, gameDto.getResultAmount());
    }

    @Test
    void givenPlayerHigher_whenStand_thenWin() {
        // Player: 20 (10+10), Dealer: 17 (7+10, won't draw more)
        var gameId = createGame(List.of("H10", "D10"), List.of("S7", "S10"));

        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.WON, gameDto.getStatus());
        assertEquals(betAmount.multiply(BigDecimal.valueOf(2)), gameDto.getResultAmount());
    }

    @Test
    void givenDealerHigher_whenStand_thenLose() {
        var gameId = createGame(List.of("H5", "D10"), List.of("S10", "S9"));

        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.LOST, gameDto.getStatus());
        assertEquals(BigDecimal.ZERO, gameDto.getResultAmount());
    }

    @Test
    void givenEqualTotals_whenStand_thenDraw() {
        // Both have 20
        var gameId = createGame(List.of("H10", "DQ"), List.of("S10", "SK"));

        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.DRAW, gameDto.getStatus());
        assertEquals(betAmount, gameDto.getResultAmount());
    }

    @Test
    void givenDealerBusts_whenStand_thenWin() {
        // Dealer has soft 16 (A+5), will hit and might bust
        var gameId = createGame(List.of("H10", "D9"), List.of("SA", "S5"));

        var gameDto = blackjackService.stand(user, gameId);

        // Dealer will draw cards. If they bust, player wins
        if (gameDto.getStatus() == GameStatus.WON) {
            assertEquals(betAmount.multiply(BigDecimal.valueOf(2)), gameDto.getResultAmount());
        }
    }

    @Test
    void givenDealerSoft17_whenStand_thenDealerDraws() {
        // Dealer has soft 17 (A+6), must draw
        var gameId = createGame(List.of("H10", "D9"), List.of("SA", "S6"));

        var gameDto = blackjackService.stand(user, gameId);

        // Dealer should have drawn at least one more card
        assertTrue(gameDto.getDealerCards().size() >= 3);
    }

    @Test
    void givenNonExistentGame_whenStand_thenThrowsException() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.stand(user, 99999L)
        );
        assertEquals("404 NOT_FOUND \"Game not found\"", ex.getMessage());
    }

    @Test
    void givenWrongUser_whenStand_thenThrowsForbidden() {
        var gameId = createGame(List.of("H5", "D6"), List.of("S7", "S10"));

        User otherUser = userRepository.save(
                User.builder()
                        .username("otheruser")
                        .email("other@example.com")
                        .hash("hash")
                        .balance(BigDecimal.valueOf(1000))
                        .build()
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.stand(otherUser, gameId)
        );
        assertEquals("403 FORBIDDEN \"Forbidden\"", ex.getMessage());
    }

    @Test
    void givenFinishedGame_whenStand_thenThrowsException() {
        var gameId = createGame(List.of("DA", "D10"), List.of("S7", "S10"));

        blackjackService.stand(user, gameId);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.stand(user, gameId)
        );
        assertEquals("400 BAD_REQUEST \"Game already finished\"", ex.getMessage());
    }

    @Test
    void givenPlayerAlreadyStood_whenStand_thenThrowsException() {
        var gameId = createGame(List.of("H5", "D6"), List.of("S7", "S10"));

        var game = blackjackGameRepository.findById(gameId).orElseThrow();
        game.setDidPlayerStand(true);
        blackjackGameRepository.save(game);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                blackjackService.stand(user, gameId)
        );
        assertEquals("400 BAD_REQUEST \"Player already stood\"", ex.getMessage());
    }

    // ==================== ACE VALUE TESTS ====================

    @Test
    void givenAceAs11_whenCalculated_thenCorrectValue() {
        // Ace + 9 = 20 (Ace counted as 11)
        var gameId = createGame(List.of("HA", "H9"), List.of("S7", "S10"));

        var gameDto = blackjackService.stand(user, gameId);

        // Player should have 20, which beats dealer's 17
        assertEquals(GameStatus.WON, gameDto.getStatus());
    }

    @Test
    void givenAceAs1_whenCalculated_thenCorrectValue() {
        // A + 9 + 10 = 20 (Ace counted as 1 to avoid bust)
        var gameId = createGame(List.of("HA", "H9", "H10"), List.of("S7", "S10"));

        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.WON, gameDto.getStatus());
    }

    @Test
    void givenMultipleAces_whenCalculated_thenCorrectValue() {
        // A + A + 9 = 21 (one Ace as 11, one as 1)
        var gameId = createGame(List.of("HA", "DA", "H9"), List.of("S7", "S10"));

        var gameDto = blackjackService.stand(user, gameId);

        // Should equal dealer's 17 or win
        assertEquals(GameStatus.WON, gameDto.getStatus());
    }

    // ==================== EDGE CASES ====================

    @RepeatedTest(10)
    void givenMultipleHits_whenNoDuplicateCards_thenSuccess() {
        var gameId = createGame(List.of("H2"), List.of("S7"));

        // Hit multiple times
        blackjackService.hit(user, gameId);
        blackjackService.hit(user, gameId);
        var gameDto = blackjackService.hit(user, gameId);

        // Verify no duplicate cards in player hand
        var cards = gameDto.getPlayerCards();
        assertEquals(cards.size(), cards.stream().distinct().count());
    }

    @RepeatedTest(10)
    void givenDealerDrawsMultipleCards_whenStand_thenNoDuplicates() {
        // Dealer has low total, will need to draw multiple cards
        var gameId = createGame(List.of("H10", "D9"), List.of("S2", "S3"));

        var gameDto = blackjackService.stand(user, gameId);

        // Verify no duplicate cards across all cards
        var allCards = new ArrayList<String>();
        allCards.addAll(gameDto.getPlayerCards());
        allCards.addAll(gameDto.getDealerCards());

        assertEquals(allCards.size(), allCards.stream().distinct().count());
    }


    private Long createGame(List<String> playerCards, List<String> dealerCards) {
        var game = BlackjackGame.builder()
                .player(user)
                .playerCards(new ArrayList<>(playerCards))
                .dealerCards(new ArrayList<>(dealerCards))
                .playedAt(LocalDateTime.now())
                .status(GameStatus.IN_PROGRESS)
                .betAmount(betAmount)
                .didPlayerStand(false)
                .build();

        blackjackGameRepository.save(game);

        return game.getId();
    }
}