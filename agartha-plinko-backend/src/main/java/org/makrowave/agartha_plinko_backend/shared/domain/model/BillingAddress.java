package org.makrowave.agartha_plinko_backend.shared.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "billing_address")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private Long userId;

    @OneToOne(mappedBy = "billingAddress")
    private User user;
}
