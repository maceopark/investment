package com.maceo.investment.datacrawler.model;

import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Getter @Setter
@ToString(exclude = "stocks")
@NoArgsConstructor
public class Market {

    private String marketId;
    private String marketDesc;
    private String codeCrawlBaseUrl;
    private List<Stock> stocks = new ArrayList<>();

    public String getStockListPageURL(Integer page) {
        Objects.requireNonNull(page);
        return getCodeCrawlBaseUrl().replace("#{pageNum}", page.toString());
    }

    public boolean hasStock(Stock stock) {
        return getStocks().stream().filter(s -> s.getMarketId().equals(stock.getMarketId()) && s.getStockCode().equals(stock.getStockCode())).findAny().isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Market)) return false;
        if (!super.equals(o)) return false;

        Market market = (Market) o;

        return getMarketId().equals(market.getMarketId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getMarketId().hashCode();
        return result;
    }
}
