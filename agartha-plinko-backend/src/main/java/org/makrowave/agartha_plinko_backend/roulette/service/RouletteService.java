package org.makrowave.agartha_plinko_backend.roulette.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.roulette.domain.RouletteBetRequest;
import org.makrowave.agartha_plinko_backend.roulette.domain.RouletteGameDto;
import org.makrowave.agartha_plinko_backend.roulette.repository.IRouletteGameRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.GameStatus;
import org.makrowave.agartha_plinko_backend.shared.domain.RouletteBetType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.RouletteBet;
import org.makrowave.agartha_plinko_backend.shared.domain.model.RouletteGame;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouletteService implements IRouletteService {

    @Autowired
    private final IRouletteGameRepository rouletteGameRepository;

    private static final Map<Integer, String> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put(0, "GREEN");

        List<Integer> reds = Arrays.asList(
                1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36
        );

        for (int i = 1; i <= 36; i++) {
            if (reds.contains(i)) COLOR_MAP.put(i, "RED");
            else COLOR_MAP.put(i, "BLACK");
        }
    }

    private String getColor(int n) {
        return COLOR_MAP.getOrDefault(n, "GREEN");
    }

    private int rollNumber() {
        return new Random().nextInt(37);
    }

    @Override
    @Transactional
    public RouletteGameDto placeBet(User player, RouletteBetRequest betReq) {

        if (betReq.getBetAmount() == null || betReq.getBetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid bet amount");
        }

        RouletteGame game = RouletteGame.builder()
                .player(player)
                .status(GameStatus.IN_PROGRESS)
                .playedAt(LocalDateTime.now())
                .bets(new ArrayList<>())
                .rolledNumber(null)
                .rolledColor(null)
                .totalBetAmount(BigDecimal.ZERO)
                .winAmount(BigDecimal.ZERO)
                .build();

        RouletteBet bet = RouletteBet.builder()
                .game(game)
                .betType(betReq.getBetType())
                .number(betReq.getNumber())
                .betAmount(betReq.getBetAmount())
                .build();

        game.getBets().add(bet);
        game.setTotalBetAmount(betReq.getBetAmount());

        rouletteGameRepository.save(game);

        return new RouletteGameDto(game);
    }

    @Override
    public RouletteGameDto getGame(Long gameId, User player) {
        RouletteGame game = rouletteGameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!game.getPlayer().getUserId().equals(player.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        return new RouletteGameDto(game);
    }

    @Override
    @Transactional
    public RouletteGameDto spin(User player, Long gameId) {

        RouletteGame game = rouletteGameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!game.getPlayer().getUserId().equals(player.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game already finished");
        }

        int rolled = rollNumber();
        String color = getColor(rolled);

        game.setRolledNumber(rolled);
        game.setRolledColor(color);

        BigDecimal totalWin = BigDecimal.ZERO;

        for (RouletteBet bet : game.getBets()) {
            BigDecimal win = resolveBet(bet, rolled, color);
            bet.setWonAmount(win);
            totalWin = totalWin.add(win);
        }

        game.setWinAmount(totalWin);

        if (totalWin.compareTo(BigDecimal.ZERO) == 0) {
            game.setStatus(GameStatus.LOST);
        } else if (totalWin.compareTo(game.getTotalBetAmount()) == 0) {
            game.setStatus(GameStatus.DRAW);
        } else {
            game.setStatus(GameStatus.WON);
        }

        rouletteGameRepository.save(game);

        return new RouletteGameDto(game);
    }

    private BigDecimal resolveBet(RouletteBet bet, int rolled, String color) {

        return switch (bet.getBetType()) {

            case NUMBER -> resolveNumberBet(bet, rolled);
            case COLOR -> resolveColorBet(bet, color);
            case EVEN_ODD -> resolveEvenOddBet(bet, rolled);
            case LOW_HIGH -> resolveLowHighBet(bet, rolled);
            case DOZEN -> resolveDozenBet(bet, rolled);
            case COLUMN -> resolveColumnBet(bet, rolled);

        };
    }

    private BigDecimal resolveNumberBet(RouletteBet bet, int rolled) {
        if (bet.getNumber() != null && bet.getNumber() == rolled) {
            return bet.getBetAmount().multiply(BigDecimal.valueOf(36));
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal resolveColorBet(RouletteBet bet, String rolledColor) {

        if ("GREEN".equalsIgnoreCase(rolledColor)) {
            return BigDecimal.ZERO;
        }

        if (bet.getNumber() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLOR bet requires number=1=RED or 2=BLACK");
        }

        String chosen = bet.getNumber() == 1 ? "RED" : "BLACK";

        if (chosen.equalsIgnoreCase(rolledColor)) {
            return bet.getBetAmount().multiply(BigDecimal.valueOf(2));
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal resolveEvenOddBet(RouletteBet bet, int rolled) {

        if (rolled == 0) return BigDecimal.ZERO;

        boolean isEven = rolled % 2 == 0;
        boolean chosenEven = (bet.getNumber() == 1); // 1=even, 2=odd

        if (isEven == chosenEven) {
            return bet.getBetAmount().multiply(BigDecimal.valueOf(2));
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal resolveLowHighBet(RouletteBet bet, int rolled) {

        if (rolled == 0) return BigDecimal.ZERO;

        boolean isLow = rolled >= 1 && rolled <= 18;
        boolean chosenLow = (bet.getNumber() == 1); // 1=LOW, 2=HIGH

        if (isLow == chosenLow) {
            return bet.getBetAmount().multiply(BigDecimal.valueOf(2));
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal resolveDozenBet(RouletteBet bet, int rolled) {

        if (rolled == 0) return BigDecimal.ZERO;

        int rolledDozen = (rolled - 1) / 12 + 1;

        if (rolledDozen == bet.getNumber()) {
            return bet.getBetAmount().multiply(BigDecimal.valueOf(3));
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal resolveColumnBet(RouletteBet bet, int rolled) {

        if (rolled == 0) return BigDecimal.ZERO;

        int rolledColumn = ((rolled - 1) % 3) + 1;

        if (rolledColumn == bet.getNumber()) {
            return bet.getBetAmount().multiply(BigDecimal.valueOf(3));
        }

        return BigDecimal.ZERO;
    }

}
