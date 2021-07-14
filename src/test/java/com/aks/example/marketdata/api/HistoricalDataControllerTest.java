package com.aks.example.marketdata.api;

import com.aks.example.marketdata.configuration.WebClientConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@Tag("integration-test")
@WebFluxTest(HistoricalDataController.class)
@Import({WebClientConfiguration.class})
class HistoricalDataControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getHistoricalData_success_test() {

        webTestClient.get()
                .uri("/marketData/eod/latest?symbol=AAPL")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StockDto.class)
                .value(stockDto -> {
                    Assertions.assertNotNull(stockDto);
                    stockDto.getData().forEach(Assertions::assertNotNull);
                });
    }

    @Test
    public void getHistoricalData_invalid_symbol_test() {

        webTestClient.get()
                .uri("/marketData/eod/latest?symbol=AXV")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(errorResponse -> Assertions.assertEquals("At least one valid symbol must be provided", errorResponse));
    }
}
