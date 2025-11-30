package org.makrowave.agartha_plinko_backend.roulette.controller;

import org.makrowave.agartha_plinko_backend.roulette.domain.RouletteBetRequest;
import org.makrowave.agartha_plinko_backend.roulette.domain.RouletteGameDto;
import org.makrowave.agartha_plinko_backend.roulette.service.IRouletteService;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roulette")
public class RouletteController {

    private final IRouletteService rouletteService;

    public RouletteController(IRouletteService rouletteService) {
        this.rouletteService = rouletteService;
    }

    @PostMapping("/bet")
    public ResponseEntity<RouletteGameDto> placeBet(
            @RequestBody RouletteBetRequest request,
            @AuthenticationPrincipal User player
    ) {
        RouletteGameDto game = rouletteService.placeBet(player, request);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{gameId}/spin")
    public ResponseEntity<RouletteGameDto> spin(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User player
    ) {
        RouletteGameDto game = rouletteService.spin(player, gameId);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<RouletteGameDto> getGame(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User player
    ) {
        RouletteGameDto dto = rouletteService.getGame(gameId, player);
        return ResponseEntity.ok(dto);
    }
}
