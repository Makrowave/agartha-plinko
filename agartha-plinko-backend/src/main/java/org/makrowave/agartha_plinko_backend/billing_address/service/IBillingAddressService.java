package org.makrowave.agartha_plinko_backend.billing_address.service;

import org.makrowave.agartha_plinko_backend.billing_address.domain.*;

public interface IBillingAddressService {
    BillingAddressResponseDto create(BillingAddressCreateDto dto);

    BillingAddressResponseDto update(Long id, BillingAddressUpdateDto dto);

    BillingAddressResponseDto get(Long id);

    void delete(Long id);
}
