package org.makrowave.agartha_plinko_backend.blackjack.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.makrowave.agartha_plinko_backend.blackjack.domain.BlackjackCard;
import org.makrowave.agartha_plinko_backend.blackjack.repository.IBlackjackGameRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.AbstractCard;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.model.BlackjackGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.shared.util.CardUtil;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BlackjackServiceTest {
    @Autowired
    private BlackjackService blackjackService;

    @Autowired
    private IBlackjackGameRepository blackjackGameRepository;

    @Autowired
    private IUserRepository userRepository;

    private Long userId;

    @Transactional
    protected Long createGame(List<String> playerCards, List<String> dealerCards) {
        var user = getUser();

        var game = BlackjackGame.builder()
                .player(user)
                .playerCards(playerCards)
                .dealerCards(dealerCards)
                .playedAt(LocalDateTime.now())
                .status(GameStatus.IN_PROGRESS)
                .betAmount(BigDecimal.valueOf(100))
                .didPlayerStand(false)
                .build();

        blackjackGameRepository.save(game);

        return game.getId();
    }

    /// Creates a game where dealer won't draw more cards.
    /// 7 of spades and 10 of spades is used by the method
    @Transactional
    protected Long createAndAddGameFinishedByDealer(List<String> playerCards) {
        var dealerCards = new ArrayList<String>() {
            {
                add("S7");
                add("S10");
            }
        };

        return createGame(playerCards, dealerCards);
    }

    protected User getUser() {
        var user = userRepository.findById(userId);

        if(user.isEmpty()) {
            fail();
        }

        return user.get();
    }

    @BeforeEach
    void setUp() {
        var user = userRepository.save(
                org.makrowave.agartha_plinko_backend.shared.domain.model.User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .hash("dummyhash")
                        .balance(BigDecimal.valueOf(100))
                        .lastDailyFundsRedeemed(null)
                        .build()
        );
        userId = user.getUserId();
    }

    @Test
    void givenBlackjackHand_whenResolve_thenWin() {
        var hand = new ArrayList<String>() {
            {
                add("DA");
                add("D10");
            }
        };

        var gameId = createAndAddGameFinishedByDealer(hand);
        var user = getUser();
        var gameDto = blackjackService.stand(user, gameId);

        assertEquals(GameStatus.WON, gameDto.getStatus());
    }
}
