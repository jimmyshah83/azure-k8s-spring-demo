package com.aks.example.marketdata.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InvalidSymbolException extends Throwable {

    private final ErrorResponse errorResponse;
}
