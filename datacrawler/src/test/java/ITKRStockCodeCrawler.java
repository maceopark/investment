import com.maceo.investment.datacrawler.KRStockCodeCrawl;
import com.maceo.investment.datacrawler.Market;
import com.maceo.investment.datacrawler.Stock;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class ITKRStockCodeCrawler {

    private Market KOSPI;
    private Market KOSDAQ;

    @Before
    public void setUp() {
        KOSPI = new Market();
        KOSPI.setMarketId("KOSPI");
        KOSPI.setCodeCrawlBaseUrl("http://finance.naver.com/sise/sise_market_sum.nhn?sosok=0&page=#{pageNum}");

        KOSDAQ = new Market();
        KOSDAQ.setMarketId("KOSDAQ");
        KOSDAQ.setCodeCrawlBaseUrl("http://finance.naver.com/sise/sise_market_sum.nhn?sosok=1&page=#{pageNum}");
    }
    @Test
    public void getLastPageNumSucceedsWithProperURL() {
        Try<Integer> lastPageNum = KRStockCodeCrawl.getLastPageNum.apply(KOSPI);
        assertThat(lastPageNum.isSuccess()).isTrue();
        Try<Integer> lastPageNum2 = KRStockCodeCrawl.getLastPageNum.apply(KOSDAQ);
        assertThat(lastPageNum2.isSuccess()).isTrue();
    }

    @Test
    public void getStockFromPageSucceedsWithProperURL() {
        Try<List<Stock>> parsedStocks = KRStockCodeCrawl.getStocksFromPage.apply(KOSPI, 1);
        assertThat(parsedStocks.isSuccess()).isTrue();
        assertThat(parsedStocks.get()).isNotEmpty();
    }

    @Test
    public void getStockCodeFromMarket() {
        Try<List<Stock>> result = KRStockCodeCrawl.getStockCodes.apply(KOSPI);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get().size()).isGreaterThan(1000);

        Try<List<Stock>> result2 = KRStockCodeCrawl.getStockCodes.apply(KOSDAQ);
        assertThat(result2.isSuccess()).isTrue();
        assertThat(result2.get().size()).isGreaterThan(500);
    }
}
