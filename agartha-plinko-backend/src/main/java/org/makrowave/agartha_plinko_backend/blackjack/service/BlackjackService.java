package org.makrowave.agartha_plinko_backend.blackjack.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.blackjack.domain.BlackjackCard;
import org.makrowave.agartha_plinko_backend.blackjack.domain.BlackjackGameDto;
import org.makrowave.agartha_plinko_backend.blackjack.repository.IBlackjackGameRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.*;
import org.makrowave.agartha_plinko_backend.shared.domain.model.BlackjackGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.shared.util.CardUtil;
import org.makrowave.agartha_plinko_backend.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BlackjackService implements IBlackjackService {

    @Autowired
    private final IBlackjackGameRepository blackjackGameRepository;

    @Autowired
    private final IWalletService walletService;

    private final BigDecimal VICTORY_MULTIPLIER = BigDecimal.valueOf(2);

    @Transactional
    public BlackjackGameDto createGame(User player, BigDecimal betAmount) {
        List<AbstractCard> deck = createDeck();
        Collections.shuffle(deck);

        List<AbstractCard> playerCards = new ArrayList<>();
        List<AbstractCard> dealerCards = new ArrayList<>();
        playerCards.add(drawCard(deck));
        playerCards.add(drawCard(deck));
        dealerCards.add(drawCard(deck));
        dealerCards.add(drawCard(deck));


        BlackjackGame game = BlackjackGame.builder()
                .player(player)
                .betAmount(betAmount)
                .playerCards(CardUtil.cardsToStrings(playerCards))
                .dealerCards(CardUtil.cardsToStrings(dealerCards))
                .status(GameStatus.IN_PROGRESS)
                .playedAt(LocalDateTime.now())
                .build();

        blackjackGameRepository.save(game);

        walletService.deductBet(
                player.getUserId(),
                betAmount,
                GameType.BLACKJACK,
                game.getId()
        );

        return new BlackjackGameDto(game);
    }

    @Transactional
    public BlackjackGameDto hit(User player, Long gameId) {
        BlackjackGame game = blackjackGameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!game.getPlayer().getUserId().equals(player.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game already finished");
        }

        if (game.isDidPlayerStand()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player already stood");
        }

        List<AbstractCard> deck = rebuildDeckExcluding(
                CardUtil.stringsToCards(game.getPlayerCards(), BlackjackCard::fromString),
                CardUtil.stringsToCards(game.getPlayerCards(), BlackjackCard::fromString)
        );

        AbstractCard drawn = drawCard(deck);

        List<String> playerCards = new ArrayList<>(game.getPlayerCards());
        playerCards.add(drawn.toString());
        game.setPlayerCards(playerCards);

        int total = calculateHandValue(
                CardUtil.stringsToCards(game.getPlayerCards(), BlackjackCard::fromString)
        );

        if (total > 21) {
            game.setDidPlayerStand(true);
            game.setStatus(GameStatus.LOST);
            game.setResultAmount(BigDecimal.ZERO);

            walletService.settleBet(
                    player.getUserId(),
                    game.getResultAmount(),
                    GameType.BLACKJACK,
                    game.getId()
            );
        }

        blackjackGameRepository.save(game);

        return new BlackjackGameDto(game);
    }

    @Transactional
    public BlackjackGameDto stand(User player, Long gameId) {
        BlackjackGame game = blackjackGameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!game.getPlayer().getUserId().equals(player.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game already finished");
        }

        if (game.isDidPlayerStand()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player already stood");
        }

        game.setDidPlayerStand(true);

        List<AbstractCard> deck = rebuildDeckExcluding(
                CardUtil.stringsToCards(game.getPlayerCards(), BlackjackCard::fromString),
                CardUtil.stringsToCards(game.getDealerCards(), BlackjackCard::fromString)
        );

        List<AbstractCard> dealerCards = new ArrayList<>(CardUtil.stringsToCards(game.getDealerCards(),
                BlackjackCard::fromString));

        List<AbstractCard> playerCards = new ArrayList<>(CardUtil.stringsToCards(game.getPlayerCards(),
                BlackjackCard::fromString));

        int dealerTotal = calculateHandValue(dealerCards);
        int playerTotal = calculateHandValue(playerCards);

        while(dealerTotal < 17 || (dealerTotal == 17 && isSoft17(dealerCards))) {
            AbstractCard card = drawCard(deck);
            dealerCards.add(card);
            dealerTotal = calculateHandValue(dealerCards);
        }

        // Create a new mutable ArrayList from the converted strings
        game.setDealerCards(new ArrayList<>(CardUtil.cardsToStrings(dealerCards)));

        resolveGame(game, playerCards, dealerCards);

        return new BlackjackGameDto(game);
    }

    private void resolveGame(BlackjackGame game, List<AbstractCard> playerCards, List<AbstractCard> dealerCards) {
        int playerTotal = calculateHandValue(playerCards);
        int dealerTotal = calculateHandValue(dealerCards);

        boolean playerBlackjack = (playerCards.size() == 2 && playerTotal == 21);
        boolean dealerBlackjack = (dealerCards.size() == 2 && dealerTotal == 21);

        if (playerBlackjack && dealerBlackjack) {
            game.setStatus(GameStatus.DRAW);
            game.setResultAmount(game.getBetAmount());
        } else if (playerBlackjack) {
            game.setStatus(GameStatus.WON);
            game.setResultAmount(game.getBetAmount().multiply(VICTORY_MULTIPLIER));
        } else if (dealerBlackjack) {
            game.setStatus(GameStatus.LOST);
            game.setResultAmount(BigDecimal.ZERO);
        } else if (dealerTotal > 21 || playerTotal > dealerTotal) {
            game.setStatus(GameStatus.WON);
            game.setResultAmount(game.getBetAmount().multiply(VICTORY_MULTIPLIER));
        } else if (dealerTotal > playerTotal) {
            game.setStatus(GameStatus.LOST);
            game.setResultAmount(BigDecimal.ZERO);
        } else {
            // totals are equal → draw
            game.setStatus(GameStatus.DRAW);
            game.setResultAmount(game.getBetAmount());
        }

        walletService.settleBet(
                game.getPlayer().getUserId(),
                game.getResultAmount(),
                GameType.BLACKJACK,
                game.getId()
        );

        blackjackGameRepository.save(game);
    }


    private boolean isSoft17(List<AbstractCard> dealerCards) {
        int total = 0;
        boolean hasAce = false;

        for (AbstractCard card : dealerCards) {
            CardRank rank = card.getRank();
            if (rank == CardRank.ACE) {
                hasAce = true;
            }
            total += card.getValue();
        }

        return hasAce && total == 17;
    }

    private AbstractCard drawCard(List<AbstractCard> deck) {
        return deck.removeLast();
    }

    private List<AbstractCard> createDeck() {
        List<AbstractCard> deck = new ArrayList<>();

        for (CardSuit suit : CardSuit.values()) {
            for (CardRank rank : CardRank.values()) {
                deck.add(new BlackjackCard(suit, rank));
            }
        }
        return deck;
    }

    private List<AbstractCard> rebuildDeckExcluding(List<AbstractCard> playerCards,
                                                     List<AbstractCard> dealerCards) {
        List<AbstractCard> deck = createDeck();

        Set<String> used = new HashSet<>();

        for (AbstractCard c : playerCards) used.add(c.toString());
        for (AbstractCard c : dealerCards) used.add(c.toString());

        deck.removeIf(c -> used.contains(c.toString()));

        Collections.shuffle(deck);
        return deck;
    }


    private int calculateHandValue(List<AbstractCard> hand) {
        int value = 0;
        int aceCount = 0;

        for (AbstractCard card : hand) {
            value += card.getValue();
            if (card.getRank() == CardRank.ACE) {
                aceCount++;
            }
        }

        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }

        return value;
    }

}
