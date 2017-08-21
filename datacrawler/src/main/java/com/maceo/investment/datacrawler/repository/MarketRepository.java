package com.maceo.investment.datacrawler.repository;

import com.maceo.investment.datacrawler.Market;
import com.maceo.investment.datacrawler.Stock;
import com.maceo.investment.datacrawler.StockLastCrawlDate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MarketRepository {

    List<Market> getMarkets();

    void insertStock(Stock stock);

    List<StockLastCrawlDate> getStocksToCrawlDailyData(@Param("queryDate") String queryDate);

    Stock getStock(@Param("stockid") String stockId);

}
