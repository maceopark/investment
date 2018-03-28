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
import org.springframework.test.context.jdbc.Sql;
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
    @Sql(statements = {"merge into datacrawl.stock s using (select '276650' as stock_code, 'KOSPI' as market_id, 'a0916a92-3cf0-4e2b-ba6b-cf08f6b449a0' as stock_id, 'test stock' as stock_name from dual) a on (s.stock_code = a.stock_code and s.market_id = a.market_id) when not matched then insert (stock_code, market_id, stock_id, stock_name) values ('276650' , 'KOSPI' , 'a0916a92-3cf0-4e2b-ba6b-cf08f6b449a0', 'test stock')"})
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
