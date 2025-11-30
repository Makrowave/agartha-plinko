package org.makrowave.agartha_plinko_backend.wallet.repository;

import org.makrowave.agartha_plinko_backend.shared.domain.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IWalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Page<WalletTransaction> findByUserUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
