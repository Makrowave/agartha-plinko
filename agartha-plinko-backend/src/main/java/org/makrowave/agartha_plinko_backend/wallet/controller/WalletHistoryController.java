package org.makrowave.agartha_plinko_backend.wallet.controller;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.wallet.domain.WalletTransactionDto;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.wallet.repository.IWalletTransactionRepository;
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
public class WalletHistoryController {

    private final IWalletTransactionRepository walletTransactionRepository;

    @GetMapping("/wallet")
    public Page<WalletTransactionDto> getWalletHistory(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return walletTransactionRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId(), pageable)
                .map(WalletTransactionDto::fromEntity);
    }
}