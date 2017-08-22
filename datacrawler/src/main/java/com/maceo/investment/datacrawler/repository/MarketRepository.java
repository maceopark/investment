package com.maceo.investment.datacrawler.repository;

import com.maceo.investment.datacrawler.model.Market;
import com.maceo.investment.datacrawler.model.Stock;
import com.maceo.investment.datacrawler.model.StockDailyData;
import com.maceo.investment.datacrawler.model.StockLastCrawlDate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MarketRepository {

    List<Market> getMarkets();

    void insertStock(Stock stock);

    List<StockLastCrawlDate> getStocksToCrawlDailyData(@Param("queryDate") DateTime queryDate);

    Stock getStock(@Param("stockId") String stockId);

    Stock getStockByStockCode(@Param("stockCode") String stockCode, @Param("marketId") String marketId);

    void insertStockDailyData(@Param("stockId") UUID stockId, @Param("data") StockDailyData data);
}
