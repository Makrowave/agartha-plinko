package org.makrowave.agartha_plinko_backend.billing_address.repository;

import org.makrowave.agartha_plinko_backend.shared.domain.model.BillingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBillingAddressRepository extends JpaRepository<BillingAddress, Long> {
}
