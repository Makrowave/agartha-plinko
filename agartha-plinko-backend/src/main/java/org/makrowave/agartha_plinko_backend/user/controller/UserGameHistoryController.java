package org.makrowave.agartha_plinko_backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.user.domain.UserGameHistoryDto;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.user.repository.IUserGameHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class UserGameHistoryController {

    private final IUserGameHistoryRepository gameHistoryRepository;

    @GetMapping("/games")
    public Page<UserGameHistoryDto> getGameHistory(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return gameHistoryRepository.findByUserUserIdOrderByPlayedAtDesc(user.getUserId(), pageable)
                .map(UserGameHistoryDto::fromEntity);
    }
}