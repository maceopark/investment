package com.maceo.investment.datacrawler.repository;

import com.maceo.investment.datacrawler.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MarketRepository {

    String STOCK_ID = "stockId";

    List<Market> getMarkets();

    void insertStock(Stock stock);

    List<StockLastCrawlDate> getStocksToCrawlDailyData(@Param("queryDate") DateTime queryDate);

    Stock getStock(@Param(STOCK_ID) String stockId);

    Stock getStockByStockCode(@Param("stockCode") String stockCode, @Param("marketId") String marketId);

    void insertStockDailyData(@Param(STOCK_ID) UUID stockId, @Param("data") StockDailyData data);

    List<FinancialSheet> getFinancialSheetsByStock(@Param(STOCK_ID) UUID stockId);

    void insertFinancialSheet(@Param("fsheet") FinancialSheet fsheet);

    String getStandarTerm(@Param("term") String term);

    void saveFinancialSheetItem(@Param("financialSheetId") UUID financialSheetId, @Param("item") FinancialSheetItem item);
}
