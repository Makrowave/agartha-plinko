package org.makrowave.agartha_plinko_backend.blackjack.controller;

import org.makrowave.agartha_plinko_backend.blackjack.domain.BlackjackGameDto;
import org.makrowave.agartha_plinko_backend.blackjack.service.IBlackjackService;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/blackjack")
public class BlackjackController {

    private final IBlackjackService blackjackService;

    public BlackjackController(IBlackjackService blackjackService) {
        this.blackjackService = blackjackService;
    }

    @PostMapping("/create")
    public ResponseEntity<BlackjackGameDto> createGame(
            @RequestParam BigDecimal betAmount,
            @AuthenticationPrincipal User player
    ) {
        BlackjackGameDto game = blackjackService.createGame(player, betAmount);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{gameId}/hit")
    public ResponseEntity<BlackjackGameDto> hit(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User player
    ) {
        System.out.println(player.getUserId());

        BlackjackGameDto game = blackjackService.hit(player, gameId);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{gameId}/stand")
    public ResponseEntity<BlackjackGameDto> stand(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User player
    ) {
        BlackjackGameDto game = blackjackService.stand(player, gameId);
        return ResponseEntity.ok(game);
    }
}

