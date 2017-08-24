package com.maceo.investment.datacrawler.batch;

import com.maceo.investment.datacrawler.Utils;
import com.maceo.investment.datacrawler.exception.HtmlParseException;
import com.maceo.investment.datacrawler.model.FinancialSheet;
import com.maceo.investment.datacrawler.model.FinancialSheetPeridType;
import com.maceo.investment.datacrawler.model.FinancialSheetType;
import com.maceo.investment.datacrawler.model.Stock;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KRFinancialSheetNaverCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KRFinancialSheetNaverCrawler.class);
    private static final HashMap<Integer, String> indexTermMap = new HashMap<>();
    static {
        // row index, row name
        indexTermMap.putIfAbsent(0, "타이틀");
        indexTermMap.putIfAbsent(1, "연간/분기재무제표리스트");
        indexTermMap.putIfAbsent(2, "매출액");
        indexTermMap.putIfAbsent(3, "영업이익");
        indexTermMap.putIfAbsent(4, "세전계속사업이익");
        indexTermMap.putIfAbsent(5, "당기순이익");
        indexTermMap.putIfAbsent(6, "당기순이익(지배)");
        indexTermMap.putIfAbsent(7, "당기순이익(비지배)");
        indexTermMap.putIfAbsent(8, "자산총계");
        indexTermMap.putIfAbsent(9, "부채총계");
        indexTermMap.putIfAbsent(10, "자본총계");
        indexTermMap.putIfAbsent(11, "자본총계(지배)");
        indexTermMap.putIfAbsent(12, "자본총계(비지배)");
        indexTermMap.putIfAbsent(13, "자본금");
        indexTermMap.putIfAbsent(14, "영업활동현금흐름");
        indexTermMap.putIfAbsent(15, "투자활동현금흐름");
        indexTermMap.putIfAbsent(16, "재무활동현금흐름");
        indexTermMap.putIfAbsent(17, "CAPEX");
        indexTermMap.putIfAbsent(18, "FCF");
        indexTermMap.putIfAbsent(19, "이자발생부채");
        indexTermMap.putIfAbsent(20, "영업이익률");
        indexTermMap.putIfAbsent(21, "순이익률");
        indexTermMap.putIfAbsent(22, "ROE(%)");
        indexTermMap.putIfAbsent(23, "ROA(%)");
        indexTermMap.putIfAbsent(24, "부채비율");
        indexTermMap.putIfAbsent(25, "자본유보율");
        indexTermMap.putIfAbsent(26, "EPS(원)");
        indexTermMap.putIfAbsent(27, "PER(배)");
        indexTermMap.putIfAbsent(28, "BPS(원)");
        indexTermMap.putIfAbsent(29, "PBR(배)");
        indexTermMap.putIfAbsent(30, "현금DPS(원)");
        indexTermMap.putIfAbsent(31, "현금배당수익률");
        indexTermMap.putIfAbsent(32, "현금배당성향(%)");
        indexTermMap.putIfAbsent(33, "발행주식수(보통주)");
    }

    /*
     * cmp_cd 	종목코드 	005930 (종목코드)
     * fin_typ 	재무제표 타입 	0: 주재무제표, 1: GAAP개별, 2: GAAP연결, 3: IFRS별도, 4:IFRS연결
     * freq_typ 기간 	Y:년, Q:분기
     */
    private static final String URL = "http://companyinfo.stock.naver.com/v1/company/ajax/cF1001.aspx?cmp_cd=#{stockCode}&fin_typ=0&freq_typ=#{period}";

    @Autowired
    MarketRepository marketRepository;

    public static String getUrl(String stockCode, String period) {
        String result = URL.replace("#{stockCode}", stockCode).replace("#{period}", period);
        LOGGER.trace(result);
        return result;
    }

    public static Try<Document> getFinancialSheet(String stockCode, FinancialSheetPeridType type) {
        return Try.of(() -> Jsoup.connect(getUrl(stockCode, type.getNaverURLqueryType())).get());
    }

    public static List<FinancialSheet> parseFSheetListRow(FinancialSheetType sheetType
                                                        , FinancialSheetPeridType peridType
                                                        , final Stock stock
                                                        , Element firstRow) {
        return firstRow.select("th").stream().map(title -> {
            FinancialSheet f = new FinancialSheet();
            f.setFinancialSheetId(UUID.randomUUID());
            f.setStockId(stock.getStockId());
            f.parseNaverYearQuarterString(peridType, Utils.norm(title.text()));
            f.setFinancialSheetType(sheetType);
            f.setCreatedDate(DateTime.now());
            return f;
        }).collect(Collectors.toList());
    }

    public void doImport(final Stock stock) {
        final int FSHEET_LIST_NDEX = 1;

        FinancialSheetPeridType peridType = FinancialSheetPeridType.QUARTERLY;
        FinancialSheetType sheetType = FinancialSheetType.CORE_INFO;

        Try<Document> fsheet = getFinancialSheet(stock.getStockCode(), peridType);
        if(fsheet.isFailure()) {
            LOGGER.error(String.format("Getting financial sheets for %s has failed", stock.getStockName()));
            LOGGER.error(fsheet.getCause().getMessage());
            return;
        }

        Elements rows = fsheet.get().select("tr");
        if(rows.size() != indexTermMap.size()) {
            throw new HtmlParseException(String.format("Predefined format and actual HTML format doesn't match. Fix parsing logic"));
        }
//            List<FinancialSheet> storedFinancialSheets = marketRepository.getFinancialSheetsByStock(stock.getStockId());

        // Parse list of financial sheets
        List<FinancialSheet> financialSheets = parseFSheetListRow(sheetType, peridType, stock, rows.get(FSHEET_LIST_NDEX));
        //TODO: save financialSheets
        //TODO: parse rest of the document -- 매출액,
    }

    public void run() {
        marketRepository.getMarkets().stream().forEach(m -> {
            m.getStocks().stream().forEach(s -> {
                doImport(s);
            });
        });
    }

}
