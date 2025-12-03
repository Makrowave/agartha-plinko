package org.makrowave.agartha_plinko_backend.billing_address.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.makrowave.agartha_plinko_backend.BaseTest;
import org.makrowave.agartha_plinko_backend.billing_address.domain.BillingAddressCreateDto;
import org.makrowave.agartha_plinko_backend.billing_address.domain.BillingAddressUpdateDto;
import org.makrowave.agartha_plinko_backend.billing_address.repository.IBillingAddressRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.model.BillingAddress;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillingAddressServiceTest extends BaseTest {

    private IBillingAddressRepository repository;
    private BillingAddressService service;
    private Long userId;

    @BeforeEach
    void setUp() {
        repository = mock(IBillingAddressRepository.class);
        service = new BillingAddressService(repository);
        userId = 42L; // example user ID
    }

    @Test
    void givenValidCreateDto_whenCreate_thenAddressIsSaved() {
        var dto = new BillingAddressCreateDto();
        dto.setStreet("ul. Długa 12");
        dto.setCity("Warszawa");
        dto.setState("Mazowieckie");
        dto.setPostalCode("00-123");
        dto.setCountry("Polska");
        dto.setUserId(userId);

        var savedEntity = BillingAddress.builder()
                .id(1L)
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .userId(userId)
                .build();

        when(repository.save(any())).thenReturn(savedEntity);

        var result = service.create(dto);

        assertEquals("Warszawa", result.getCity());
        assertEquals("00-123", result.getPostalCode());
        assertEquals(1L, result.getId());
        assertEquals(userId, result.getUserId());

        ArgumentCaptor<BillingAddress> captor = ArgumentCaptor.forClass(BillingAddress.class);
        verify(repository).save(captor.capture());
        assertEquals(userId, captor.getValue().getUserId());
    }

    @Test
    void givenExistingAddress_whenUpdate_thenAddressIsUpdated() {
        var existing = BillingAddress.builder()
                .id(5L)
                .street("ul. Krótka 1")
                .city("Kraków")
                .state("Małopolskie")
                .postalCode("30-001")
                .country("Polska")
                .userId(userId)
                .build();

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);

        var dto = new BillingAddressUpdateDto();
        dto.setStreet("ul. Krótka 2");
        dto.setCity("Kraków");
        dto.setState("Małopolskie");
        dto.setPostalCode("30-002");
        dto.setCountry("Polska");
        dto.setUserId(userId);

        var result = service.update(5L, dto);

        assertEquals("ul. Krótka 2", result.getStreet());
        assertEquals("30-002", result.getPostalCode());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void givenNonexistentAddress_whenUpdate_thenThrowNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        var dto = new BillingAddressUpdateDto();
        dto.setStreet("test");
        dto.setCity("test");
        dto.setState("test");
        dto.setPostalCode("00-000");
        dto.setCountry("Polska");
        dto.setUserId(userId);

        var ex = assertThrows(ResponseStatusException.class, () -> service.update(999L, dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void givenAddressBelongingToOtherUser_whenUpdate_thenThrowForbidden() {
        var existing = BillingAddress.builder()
                .id(10L)
                .street("ul. Obca 1")
                .city("Wrocław")
                .state("Dolnośląskie")
                .postalCode("50-001")
                .country("Polska")
                .userId(99L) // another user
                .build();

        when(repository.findById(10L)).thenReturn(Optional.of(existing));

        var dto = new BillingAddressUpdateDto();
        dto.setStreet("ul. Nowa 1");
        dto.setCity("Wrocław");
        dto.setState("Dolnośląskie");
        dto.setPostalCode("50-002");
        dto.setCountry("Polska");
        dto.setUserId(userId); // current user

        var ex = assertThrows(ResponseStatusException.class, () -> service.update(10L, dto));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
