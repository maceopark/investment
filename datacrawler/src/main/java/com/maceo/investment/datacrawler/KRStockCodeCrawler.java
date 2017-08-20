package com.maceo.investment.datacrawler;

import com.maceo.investment.datacrawler.repository.MarketRepository;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;

@EnableAutoConfiguration
public class KRStockCodeCrawler implements CommandLineRunner{

    Logger LOGGER = LoggerFactory.getLogger(KRStockCodeCrawler.class);

    @Autowired
    private MarketRepository marketRepository;

    @Override
    @Transactional
    public void run(String... strings) throws Exception {
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

    public static void main(String[] args) {
        SpringApplication.run(KRStockCodeCrawler.class, args);
    }
}
