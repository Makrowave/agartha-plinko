package org.makrowave.agartha_plinko_backend.wallet.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.shared.domain.GameType;
import org.makrowave.agartha_plinko_backend.shared.domain.TransactionType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.shared.domain.model.UserGameHistory;
import org.makrowave.agartha_plinko_backend.shared.domain.model.WalletTransaction;
import org.makrowave.agartha_plinko_backend.user.repository.IUserRepository;
import org.makrowave.agartha_plinko_backend.wallet.repository.IWalletTransactionRepository;
import org.makrowave.agartha_plinko_backend.user.repository.IUserGameHistoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletService implements IWalletService {

    private final IUserRepository userRepository;
    private final IWalletTransactionRepository transactionRepository;
    private final IUserGameHistoryRepository gameHistoryRepository;

    @Transactional
    public void deductBet(Long userId, BigDecimal amount, GameType gameType, Long gameId) {

        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        BigDecimal newBalance = user.getBalance().subtract(amount);
        user.setBalance(newBalance);
        userRepository.save(user);

        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .type(TransactionType.BET)
                .amount(amount.negate())
                .balanceAfter(newBalance)
                .gameType(gameType)
                .gameId(gameId)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        UserGameHistory history = UserGameHistory.builder()
                .user(user)
                .gameType(gameType)
                .gameId(gameId)
                .betAmount(amount)
                .resultAmount(BigDecimal.ZERO) // Initially zero until game ends
                .playedAt(LocalDateTime.now())
                .build();

        gameHistoryRepository.save(history);
    }

    @Transactional
    public void settleBet(Long userId, BigDecimal winAmount, GameType gameType, Long gameId) {
        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        BigDecimal newBalance = user.getBalance().add(winAmount);
        user.setBalance(newBalance);
        userRepository.save(user);

        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .type(TransactionType.WIN)
                .amount(winAmount)
                .balanceAfter(newBalance)
                .gameType(gameType)
                .gameId(gameId)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        UserGameHistory history = gameHistoryRepository.findByGameIdAndGameType(gameId, gameType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History not found"));

        history.setResultAmount(winAmount);
        gameHistoryRepository.save(history);
    }
}