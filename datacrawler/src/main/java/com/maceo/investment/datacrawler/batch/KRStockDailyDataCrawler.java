package com.maceo.investment.datacrawler.batch;

import com.maceo.investment.datacrawler.StockLastCrawlDate;
import com.maceo.investment.datacrawler.repository.MarketRepository;
import com.maceo.investment.datacrawler.util.DateTimeUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class KRStockDailyDataCrawler  {

    private static final String NAVER_DAILY_MARKET_PRICE_URL = "http://finance.naver.com/item/sise_day.nhn?code=#{stockCode}&page=#{pageNum}";
    private static int MAX_PAGE_SIZE = 10;

    @Autowired
    MarketRepository marketRepository;

    Logger LOGGER = LoggerFactory.getLogger(KRStockDailyDataCrawler.class);

    public void run() {
        List<StockLastCrawlDate> result = marketRepository.getStocksToCrawlDailyData(DateTimeUtils.yyyymmdd(DateTimeUtils.utcNow()));
        result.parallelStream().map(stockLastCrawlDate -> {
            Integer pageNum = 1;
            while(true) {
                String url = NAVER_DAILY_MARKET_PRICE_URL.replace("#{stockCode}", stockLastCrawlDate.getStock().getStockCode())
                        .replace("#{pageNum}", pageNum.toString());
                int entryCount = 0;
                try {
                    Document doc = Jsoup.connect(url).get();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                if(entryCount < MAX_PAGE_SIZE) {
                    break;
                }
                if(stockLastCrawlDate.getLastCrawlDate())
            }
        });

        for(StockLastCrawlDate stockLastCrawlDate : result) {
//            LOGGER.error(stockLastCrawlDate.getStock().toString());
        }
        result.stream().map(stockLastCrawlDate -> {
            LOGGER.error(stockLastCrawlDate.getStock().toString());
            return (Void)null;
        });
    }
}
