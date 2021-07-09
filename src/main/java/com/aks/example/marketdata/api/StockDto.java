package com.aks.example.marketdata.api;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StockDto {

    private List<Stock> data;
}

@Data
class Stock {

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private String symbol;
    private String exchange;
}
