package com.maceo.investment.datacrawler.batch;

import com.maceo.investment.datacrawler.Market;
import com.maceo.investment.datacrawler.Stock;
import com.maceo.investment.datacrawler.repository.MarketRepository;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class KRStockCodeCrawler  {

    Logger LOGGER = LoggerFactory.getLogger(KRStockCodeCrawler.class);

    @Autowired
    private MarketRepository marketRepository;

    @Transactional
    public void run() {
        marketRepository.getMarkets().stream().forEach(market -> {
            KRStockCodeCrawl.getStockCodes.apply(market)
                .map(stocks -> {
                    Integer newStockCount = 0;
                    for(Stock stock : stocks) {
                        if(!market.hasStock(stock)) {
                            marketRepository.insertStock(stock);
                            newStockCount++;
                        }
                    }
                    return new Tuple2(newStockCount, market);
                })
                .onSuccess(tuple -> LOGGER.info(String.format("%d new stocks found in market %s", (Integer)tuple._1, ((Market)tuple._2).getMarketDesc())))
                .onFailure(e -> LOGGER.error(e.getMessage()));
        });
    }
}
