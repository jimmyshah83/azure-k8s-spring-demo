package com.aks.example.marketdata.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/marketData")
@RequiredArgsConstructor
@Api(
        value = "marketdata",
        tags = {"Marketdata API"}
)
public class HistoricalDataController {

    private final WebClient marketStackWebClient;

    @Value("${api.key}")
    private String apiKey;

    /**
     *
     * Get EOD data, for the previous day, for a stock symbol from Market Stack
     * Market stack API documentation: https://marketstack.com/documentation
     *
     * @param symbol Stock symbol
     * @return Latest end of day stock data
     */
    @GetMapping("/eod/latest")
    @ApiOperation("Fetches EOD data for the given stock")
    public ResponseEntity<Mono<StockDto>> getLatestEodData(@RequestParam String symbol) {
        final Mono<StockDto> stockDtoMono = marketStackWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/eod/latest")
                        .queryParam("access_key", apiKey)
                        .queryParam("symbols", symbol)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse ->
                                clientResponse
                                        .bodyToMono(ErrorResponse.class)
                                        .flatMap(errorResponse -> Mono.error(new InvalidSymbolException(errorResponse))))
                .bodyToMono(StockDto.class);

        return ResponseEntity.ok(stockDtoMono);
    }

    @ExceptionHandler(InvalidSymbolException.class)
    public ResponseEntity<Mono<String>> invalidSymbolExceptionHandler(InvalidSymbolException invalidSymbolException) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Mono.just(invalidSymbolException.getErrorResponse().getError().getMessage()));
    }
}
