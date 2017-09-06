package com.maceo.investment.datacrawler.batch;

import com.maceo.investment.datacrawler.Utils;
import com.maceo.investment.datacrawler.model.InterestRate;
import com.maceo.investment.datacrawler.model.InterestRateType;
import com.maceo.investment.datacrawler.repository.MarketRepository;
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

import java.io.IOException;
import java.util.Map;

import static com.maceo.investment.datacrawler.model.InterestRateType.KR_TREASURE_BOND_3Y;

@Component
public class KRTreasuryBond3YearCrawler {
    private static String URL = "http://info.finance.naver.com/marketindex/interestDailyQuote.nhn?marketindexCd=IRR_GOVT03Y&page=#{pageNum}";
    private static final Logger LOGGER = LoggerFactory.getLogger(KRTreasuryBond3YearCrawler.class);
    private static final String KOSPI = "KOSPI";

    @Autowired
    MarketRepository marketRepository;


    public void run() throws IOException {
        DateTime maxCrawlDate = Utils.yyyymmdd(marketRepository.getMaxInterestRateCrawlDate(KOSPI, KR_TREASURE_BOND_3Y).replace("-", ""));

        Integer curPageNum = 1;
        while(true) {
            String pageUrl = URL.replace("#{pageNum}", curPageNum.toString());
            LOGGER.trace("Crawling " + pageUrl);
            Document  doc = Jsoup.connect(pageUrl).get();
            Elements rows = doc.select("tr");

            if(rows.size() == 1) {              // Last page has just header row
                LOGGER.trace("Last page has been reached");
                return;
            }

            for(Element row : rows) {
                if(row.child(0).text().equals("날짜")) {      // First row is header
                    continue;
                }
                InterestRate interestRate = new InterestRate(KOSPI, KR_TREASURE_BOND_3Y, Utils.yyyymmdd(Utils.norm(row.child(0).text())), new Double(row.child(1).text()));

                if(interestRate.getYyyymmdd().isEqual(maxCrawlDate)) {
                    LOGGER.trace("Finished crawling newly added data");
                    return;
                }

                marketRepository.saveInterestRate(interestRate);
            }

            curPageNum++;
        }
    }

}
