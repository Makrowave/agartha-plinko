package org.makrowave.agartha_plinko_backend.billing_address.controller;

import org.junit.jupiter.api.*;
import org.makrowave.agartha_plinko_backend.BaseTest;
import org.makrowave.agartha_plinko_backend.billing_address.repository.IBillingAddressRepository;
import org.makrowave.agartha_plinko_backend.shared.domain.model.BillingAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BillingAddressControllerTest extends BaseTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    IBillingAddressRepository repository;

    private String token;

    @BeforeAll
    void registerUserOnce() {
        var req = """
                {
                  "username": "janek",
                  "email": "janek@example.com",
                  "password": "secret123"
                }
                """;

        var registerResponse = rest.postForEntity(
                "/api/auth/register",
                new HttpEntity<>(req, defaultJsonHeaders()),
                String.class
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertTrue(registerResponse.getBody().contains("token"));

        token = registerResponse.getBody()
                .replace("{\"token\":\"", "")
                .replace("\"}", "");

    }


    private HttpHeaders defaultJsonHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders authHeaders() {
        var headers = defaultJsonHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void givenBillingAddressId_whenGet_thenReturnAddress() {
        var entity = BillingAddress.builder()
                .street("ul. Miodowa 15")
                .city("Gdańsk")
                .state("Pomorskie")
                .postalCode("80-123")
                .country("Polska")
                .build();

        var saved = repository.save(entity);

        var response = rest.exchange(
                "/api/billing-address/" + saved.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Miodowa"));
        assertTrue(response.getBody().contains("Gdańsk"));
    }

    @Test
    void givenBillingAddressDto_whenPost_thenCreateAddress() {
        var req = """
                {
                  "street": "ul. Długa 7",
                  "city": "Warszawa",
                  "state": "Mazowieckie",
                  "postalCode": "00-001",
                  "country": "Polska"
                }
                """;

        var response = rest.exchange(
                "/api/billing-address",
                HttpMethod.POST,
                new HttpEntity<>(req, authHeaders()),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Długa"));
        assertTrue(response.getBody().contains("Warszawa"));
        assertTrue(response.getBody().contains("Polska"));
    }
}
