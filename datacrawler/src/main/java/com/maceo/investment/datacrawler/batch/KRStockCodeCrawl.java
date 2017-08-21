package com.maceo.investment.datacrawler.batch;

import com.google.common.base.Throwables;
import com.maceo.investment.datacrawler.Market;
import com.maceo.investment.datacrawler.Stock;
import com.maceo.investment.datacrawler.exception.HtmlParseException;
import io.vavr.control.Try;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KRStockCodeCrawl {

    private static final Logger LOGGER = LoggerFactory.getLogger(KRStockCodeCrawl.class);

    public static Function<Market, Try<Integer>> getLastPageNum = market -> {
        return Try.of(() -> {
            Document doc = Jsoup.connect(market.getStockListPageURL(1)).get();
            String lastPageLink = doc.select("td.pgRR > a").get(0).attributes().get("href");
            Pattern pattern = Pattern.compile("page=(\\d+)$");
            Matcher matcher = pattern.matcher(lastPageLink);
            if(matcher.find()) {
                return new Integer(matcher.group(1));
            }
            throw new HtmlParseException("LastPageNum parse failure: " + market.getCodeCrawlBaseUrl());
        });
    };

    public static BiFunction<Market, Integer, Try<List<Stock>>> getStocksFromPage = (market, pageNum) -> {
        return Try.of(() -> {
            String crawlUrl = market.getStockListPageURL(pageNum);
            LOGGER.trace(String.format("Processing %s ", crawlUrl));
            Document doc = Jsoup.connect(crawlUrl).get();
            return doc.select("a.tltle").stream()
                        .map(element ->  {
                            String code;
                            Matcher matcher = Pattern.compile("code=(\\d+)").matcher(element.toString());
                            if(matcher.find()) {
                                code = matcher.group(1);
                            } else {
                                throw new HtmlParseException("Parsing stock code has failed " + element.toString());
                            }
                            return new Stock(UUID.randomUUID(), code, element.text(), market.getMarketId());
                        }).collect(Collectors.toList());
        });
    };

    public static Function<Market, Try<List<Stock>>> getStockCodes = market -> {
        Try<Integer> lastPageNum = getLastPageNum.apply(market);
        if(!lastPageNum.isSuccess()) {
            return Try.failure(lastPageNum.getCause());
        }

        return Try.of(() -> {
            return Stream.iterate(1, p -> p + 1).limit(lastPageNum.get()).parallel()
                    .flatMap(page -> {
                        Try<List<Stock>> r = getStocksFromPage.apply(market, page);
                        if(r.isSuccess()) {
                            return r.get().stream();
                        } else {
                            Throwables.propagateIfPossible(r.getCause());
                        }
                        return Stream.empty();
                    }).collect(Collectors.toList());
        });
    };
}
