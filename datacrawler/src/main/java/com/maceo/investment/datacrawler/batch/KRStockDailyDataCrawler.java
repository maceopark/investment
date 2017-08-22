package com.maceo.investment.datacrawler.batch;

import com.maceo.investment.datacrawler.model.Stock;
import com.maceo.investment.datacrawler.model.StockDailyData;
import com.maceo.investment.datacrawler.model.StockLastCrawlDate;
import com.maceo.investment.datacrawler.repository.MarketRepository;
import com.maceo.investment.datacrawler.Utils;
import io.vavr.control.Try;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class KRStockDailyDataCrawler  {

    private static final String NAVER_DAILY_MARKET_PRICE_URL = "http://finance.naver.com/item/sise_day.nhn?code=#{stockCode}&page=#{pageNum}";
    private static int MAX_ENTRY_SIZE_PER_PAGE = 10;

    @Autowired
    MarketRepository marketRepository;

    private static Logger LOGGER = LoggerFactory.getLogger(KRStockDailyDataCrawler.class);

    @Transactional
    public void run() throws InterruptedException {
        List<StockLastCrawlDate> result = marketRepository.getStocksToCrawlDailyData(Utils.krNowYYYYMMDD());
        for(StockLastCrawlDate toCrawl : result) {
            Stock stock = toCrawl.getStock();
            DateTime lastCrawlDate = toCrawl.getLastCrawlDate();
            List<StockDailyData> crawlResult = naverSise(stock.getStockCode(), lastCrawlDate);
            crawlResult.forEach(r -> {
                marketRepository.insertStockDailyData(stock.getStockId(), r);
            });
            break;
        }
    }

    public static List<StockDailyData> naverSise(String stockCode, DateTime lastCrawlDate) throws InterruptedException {
        Integer pageNum = 1;
        List<StockDailyData> result = new ArrayList<>();
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
                    break;      // This means this is the last page. quit the while(true) loop.
                }
            } else {
                LOGGER.trace(String.format("Parsing %s failed: %s", url, doc.getCause().toString()));
            }
            pageNum++;
            // Sleep for up to 3s in random fashion between pages
            Thread.sleep((long)(Math.random() * 3000));
        }
        return result;
    }

    private static List<StockDailyData> parseNaverDailyStockPricePage(Document singlePage, DateTime lastCrawlDate) {
        List<StockDailyData> result = new ArrayList<>();
        for(Element elem : singlePage.select("tr")) {
            // VALID NAVER DAILY PRICE DATA HAS TWO ATTRIBUTES!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if(elem.attributes().size() == 0 ) continue;
            Elements cols = elem.select("td");
            // IF yyyymmdd is empty, skip it. FUCKING JSOUP converts &nbso to \u00a0. It's not empty string.
            // nbspToSpace converts it to space
            if(Utils.nbspToSpace(cols.get(0).text()).replace(".", "").trim().isEmpty()) continue;
            StockDailyData data = new StockDailyData(
                    Utils.yyyymmdd(Utils.nbspToSpace(cols.get(0).text()).replace(".", "").trim()),
                    Integer.parseInt(Utils.nbspToSpace(cols.get(1).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.nbspToSpace(cols.get(3).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.nbspToSpace(cols.get(4).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.nbspToSpace(cols.get(5).text()).replace(",","").trim()),
                    Integer.parseInt(Utils.nbspToSpace(cols.get(6).text()).replace(",","").trim())
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
