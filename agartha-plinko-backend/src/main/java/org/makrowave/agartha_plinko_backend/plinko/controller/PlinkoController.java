package org.makrowave.agartha_plinko_backend.plinko.controller;

import org.makrowave.agartha_plinko_backend.plinko.domain.PlinkoGameDto;
import org.makrowave.agartha_plinko_backend.plinko.domain.PlinkoRisk;
import org.makrowave.agartha_plinko_backend.plinko.service.IPlinkoService;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/plinko")
public class PlinkoController {

    private final IPlinkoService plinkoService;

    public PlinkoController(IPlinkoService plinkoService) {
        this.plinkoService = plinkoService;
    }

    @PostMapping("/play")
    public ResponseEntity<PlinkoGameDto> play(
            @RequestParam BigDecimal betAmount,
            @RequestParam(defaultValue = "8") Integer rows,
            @RequestParam(defaultValue = "MEDIUM") PlinkoRisk risk,
            @AuthenticationPrincipal User player
    ) {
        PlinkoGameDto game = plinkoService.play(player, betAmount, rows, risk);
        return ResponseEntity.ok(game);
    }
}