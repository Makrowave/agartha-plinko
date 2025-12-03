package org.makrowave.agartha_plinko_backend.billing_address.service;

import lombok.RequiredArgsConstructor;
import org.makrowave.agartha_plinko_backend.billing_address.domain.BillingAddressCreateDto;
import org.makrowave.agartha_plinko_backend.billing_address.domain.BillingAddressResponseDto;
import org.makrowave.agartha_plinko_backend.billing_address.domain.BillingAddressUpdateDto;
import org.makrowave.agartha_plinko_backend.billing_address.repository.IBillingAddressRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.model.BillingAddress;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BillingAddressService implements IBillingAddressService {

    private final IBillingAddressRepository repository;

    @Override
    public BillingAddressResponseDto create(BillingAddressCreateDto dto) {
        var entity = BillingAddress.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .userId(dto.getUserId())
                .build();

        var saved = repository.save(entity);
        return map(saved);
    }

    @Override
    public BillingAddressResponseDto update(Long id, BillingAddressUpdateDto dto) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!entity.getUserId().equals(dto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your billing address");
        }

        entity.setStreet(dto.getStreet());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());

        var saved = repository.save(entity);
        return map(saved);
    }

    @Override
    public BillingAddressResponseDto get(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return map(entity);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
    }

    private BillingAddressResponseDto map(BillingAddress addr) {
        return BillingAddressResponseDto.builder()
                .id(addr.getId())
                .street(addr.getStreet())
                .city(addr.getCity())
                .state(addr.getState())
                .postalCode(addr.getPostalCode())
                .country(addr.getCountry())
                .userId(addr.getUserId())
                .build();
    }
}
