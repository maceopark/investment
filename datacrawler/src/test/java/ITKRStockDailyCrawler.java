import com.maceo.investment.datacrawler.AppMain;
import com.maceo.investment.datacrawler.batch.KRStockDailyDataCrawler;
import com.maceo.investment.datacrawler.model.Stock;
import com.maceo.investment.datacrawler.model.StockDailyData;
import com.maceo.investment.datacrawler.repository.MarketRepository;
import io.vavr.control.Try;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringBootTest(classes = AppMain.class)
public class ITKRStockDailyCrawler {

    @Autowired
    MarketRepository marketRepository;

    @Test
    public void testSimpl() throws InterruptedException {
        Stock testStock = marketRepository.getStockByStockCode("276650", "KOSPI");
        List<StockDailyData> result = KRStockDailyDataCrawler.naverSise(testStock.getStockCode(), DateTime.now().minusYears(100));
        for(StockDailyData data : result) {
            marketRepository.insertStockDailyData(testStock.getStockId(), data);
        }
    }

    @Test
    public void getLastPageNum() {
        Try<Integer> result = KRStockDailyDataCrawler.parseNaverDailyStockPriceLastPageNum("043370");
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isGreaterThanOrEqualTo(10);

    }
}
