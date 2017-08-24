package com.maceo.investment.datacrawler.batch;

import com.maceo.investment.datacrawler.exception.HtmlParseException;
import com.maceo.investment.datacrawler.model.Stock;
import com.maceo.investment.datacrawler.model.StockDailyData;
import com.maceo.investment.datacrawler.repository.MarketRepository;
import com.maceo.investment.datacrawler.Utils;
import io.vavr.control.Try;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KRStockDailyDataCrawler  {

    private static final String NAVER_DAILY_MARKET_PRICE_URL = "http://finance.naver.com/item/sise_day.nhn?code=#{stockCode}&page=#{pageNum}";
    private static int MAX_ENTRY_SIZE_PER_PAGE = 10;

    @Autowired
    MarketRepository marketRepository;

    private static Logger LOGGER = LoggerFactory.getLogger(KRStockDailyDataCrawler.class);

    public void run() throws InterruptedException {
        marketRepository.getStocksToCrawlDailyData(Utils.krNowYYYYMMDD()).parallelStream()
                .forEach(toCrawl -> {
                    Stock stock = toCrawl.getStock();
                    DateTime lastCrawlDate = toCrawl.getLastCrawlDate();
                    List<StockDailyData> crawlResult = null;
                    try {
                        crawlResult = naverSise(stock.getStockCode(), lastCrawlDate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    crawlResult.forEach(r -> {
                        marketRepository.insertStockDailyData(stock.getStockId(), r);
                    });
                });
    }

    public static List<StockDailyData> naverSise(String stockCode, DateTime lastCrawlDate) throws InterruptedException {
        Integer pageNum = 1;
        List<StockDailyData> result = new ArrayList<>();
        int lastPageNum = parseNaverDailyStockPriceLastPageNum(stockCode).getOrElse(0);

        while(true) {
            String url = NAVER_DAILY_MARKET_PRICE_URL
                    .replace("#{stockCode}", stockCode)
                    .replace("#{pageNum}", pageNum.toString());

            Try<Document> doc = Try.of(() -> Jsoup.connect(url).get());
            List<StockDailyData>  parsedSinglePage;

            if(doc.isSuccess()) {
                parsedSinglePage = parseNaverDailyStockPricePage(doc.get(), lastCrawlDate);
                result.addAll(parsedSinglePage);
                LOGGER.trace(String.format("stockCode=%s, pageNum=%d", stockCode, pageNum));

                if(parsedSinglePage.size() < MAX_ENTRY_SIZE_PER_PAGE) {
                    LOGGER.trace(String.format("%s is last page", url));
                    break;      // This means parser hit last page with less than 10 entries. quit the while(true) loop.
                }

                if(pageNum >=lastPageNum) {
                    LOGGER.trace(String.format("%s is last page", url));
                    break;      // This means this is the last page. quit the while(true) loop.
                }

            } else {
                LOGGER.trace(String.format("Parsing %s failed: %s", url, doc.getCause().toString()));
            }
            pageNum++;
            // Sleep for up to 1s in random fashion between pages
            Thread.sleep((long)(Math.random() * 1000));
        }
        return result;
    }

    public static Try<Integer> parseNaverDailyStockPriceLastPageNum(String stockCode) {
        String url = NAVER_DAILY_MARKET_PRICE_URL
                .replace("#{stockCode}", stockCode)
                .replace("#{pageNum}", "1");

        return Try.of(() -> Jsoup.connect(url).get())
                .map(doc -> {
                    Elements lastPageAnchor = doc.select("td.pgRR > a");
                    if(lastPageAnchor.size() == 0) {
                        throw new HtmlParseException("No lastPage exists");
                    }
                    String lastPageLink = doc.select("td.pgRR > a").get(0).attributes().get("href");
                    Pattern pattern = Pattern.compile("page=(\\d+)$");
                    Matcher matcher = pattern.matcher(lastPageLink);
                    if(matcher.find()) {
                        return new Integer(matcher.group(1));
                    }
                    throw new HtmlParseException("No lastPage exists");
                });
    }

    private static List<StockDailyData> parseNaverDailyStockPricePage(Document singlePage, DateTime lastCrawlDate) {
        List<StockDailyData> result = new ArrayList<>();
        for(Element elem : singlePage.select("tr")) {
            // VALID NAVER DAILY PRICE DATA HAS TWO ATTRIBUTES!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if(elem.attributes().size() == 0 ) continue;
            Elements cols = elem.select("td");
            // IF yyyymmdd is empty, skip it. FUCKING JSOUP converts &nbso to \u00a0. It's not empty string.
            // norm converts it to space
            if(Utils.norm(cols.get(0).text()).replace(".", "").trim().isEmpty()) continue;
            StockDailyData data = new StockDailyData(
                    Utils.yyyymmdd(Utils.norm(cols.get(0).text()).replace(".", "").trim()),
                    Integer.parseInt(Utils.norm(cols.get(1).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.norm(cols.get(3).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.norm(cols.get(4).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.norm(cols.get(5).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.norm(cols.get(6).text()).replace(",","").trim())
            );

            // STOP CONDITION. IF CRAWLED YYYYMMDD HITS LASTCRAWLDATE STOP PROCESSING. IT'S ALREADY CRAWLED.
            if(lastCrawlDate.isEqual(data.getYyyymmdd())) {
                break;
            }
            result.add(data);
        }
        return result;
    }
}
