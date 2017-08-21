package com.maceo.investment.datacrawler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class StockLastCrawlDate {
    private final Stock stock;
    private final String lastCrawlDate;
}
