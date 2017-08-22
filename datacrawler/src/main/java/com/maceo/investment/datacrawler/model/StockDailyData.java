package com.maceo.investment.datacrawler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@Getter
public class StockDailyData {
    private final DateTime yyyymmdd;
    private final int finalPrice;
    private final int startPrice;
    private final int highPrice;
    private final int lowPrice;
    private final int tradeVolume;
}
