package com.maceo.investment.datacrawler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@Getter
public class StockLastCrawlDate {
    private final Stock stock;
    private final DateTime lastCrawlDate;
}
