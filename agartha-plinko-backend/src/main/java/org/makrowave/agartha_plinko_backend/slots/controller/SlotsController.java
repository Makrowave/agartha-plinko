package org.makrowave.agartha_plinko_backend.slots.controller;

import org.makrowave.agartha_plinko_backend.slots.domain.SlotsGameDto;
import org.makrowave.agartha_plinko_backend.slots.service.ISlotsService;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/slots")
public class SlotsController {

    private final ISlotsService slotsService;

    public SlotsController(ISlotsService slotsService) {
        this.slotsService = slotsService;
    }

    @PostMapping("/spin")
    public ResponseEntity<SlotsGameDto> spin(
            @RequestParam BigDecimal betAmount,
            @AuthenticationPrincipal User player
    ) {
        SlotsGameDto game = slotsService.spin(player, betAmount);
        return ResponseEntity.ok(game);
    }
}