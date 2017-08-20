package com.maceo.investment.datacrawler.repository;

import com.maceo.investment.datacrawler.Market;
import com.maceo.investment.datacrawler.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MarketRepository {

    List<Market> getMarkets();

    void insertStock(Stock stock);

}
