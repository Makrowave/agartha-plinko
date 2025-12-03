package org.makrowave.agartha_plinko_backend.billing_address.controller;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.billing_address.domain.*;
import org.makrowave.agartha_plinko_backend.billing_address.service.IBillingAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/billing-address")
@RequiredArgsConstructor
public class BillingAddressController {

    private final IBillingAddressService service;

    @PostMapping
    public ResponseEntity<BillingAddressResponseDto> create(
            @Valid @RequestBody BillingAddressCreateDto dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingAddressResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody BillingAddressUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingAddressResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
