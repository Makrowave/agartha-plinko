package org.makrowave.agartha_plinko_backend.billing_address.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingAddressResponseDto {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Long userId;
}
