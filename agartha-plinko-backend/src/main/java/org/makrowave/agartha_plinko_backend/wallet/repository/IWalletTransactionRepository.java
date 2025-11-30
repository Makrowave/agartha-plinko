package org.makrowave.agartha_plinko_backend.wallet.repository;

import jakarta.persistence.LockModeType;
import org.makrowave.agartha_plinko_backend.shared.domain.model.User;
import org.makrowave.agartha_plinko_backend.shared.domain.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IWalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

}
