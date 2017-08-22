package com.maceo.investment.datacrawler.model;

import lombok.*;
import org.joda.time.DateTime;

import java.util.UUID;

@Data
@Getter
@ToString
@AllArgsConstructor
public class Stock {
    private final UUID stockId;
    private final String stockCode;
    private final String stockName;
    private final String marketId;
    private final DateTime createdDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        if (!super.equals(o)) return false;

        Stock stock = (Stock) o;

        if (!getStockCode().equals(stock.getStockCode())) return false;
        return getMarketId().equals(stock.getMarketId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getStockCode().hashCode();
        result = 31 * result + getMarketId().hashCode();
        return result;
    }
}
