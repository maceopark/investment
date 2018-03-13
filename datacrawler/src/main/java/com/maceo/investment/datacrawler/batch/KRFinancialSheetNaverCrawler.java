package com.maceo.investment.datacrawler.batch;

import com.maceo.investment.datacrawler.Utils;
import com.maceo.investment.datacrawler.exception.HtmlParseException;
import com.maceo.investment.datacrawler.model.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KRFinancialSheetNaverCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KRFinancialSheetNaverCrawler.class);
    private static final HashMap<Integer, String> rowIndexTermMap = new HashMap<>();
    static {
        // row index, row name
        rowIndexTermMap.putIfAbsent(0, "타이틀");
        rowIndexTermMap.putIfAbsent(1, "연간/분기재무제표리스트");
        rowIndexTermMap.putIfAbsent(2, "매출액");
        rowIndexTermMap.putIfAbsent(3, "영업이익");
        rowIndexTermMap.putIfAbsent(4, "세전계속사업이익");
        rowIndexTermMap.putIfAbsent(5, "당기순이익");
        rowIndexTermMap.putIfAbsent(6, "당기순이익(지배)");
        rowIndexTermMap.putIfAbsent(7, "당기순이익(비지배)");
        rowIndexTermMap.putIfAbsent(8, "자산총계");
        rowIndexTermMap.putIfAbsent(9, "부채총계");
        rowIndexTermMap.putIfAbsent(10, "자본총계");
        rowIndexTermMap.putIfAbsent(11, "자본총계(지배)");
        rowIndexTermMap.putIfAbsent(12, "자본총계(비지배)");
        rowIndexTermMap.putIfAbsent(13, "자본금");
        rowIndexTermMap.putIfAbsent(14, "영업활동현금흐름");
        rowIndexTermMap.putIfAbsent(15, "투자활동현금흐름");
        rowIndexTermMap.putIfAbsent(16, "재무활동현금흐름");
        rowIndexTermMap.putIfAbsent(17, "CAPEX");
        rowIndexTermMap.putIfAbsent(18, "FCF");
        rowIndexTermMap.putIfAbsent(19, "이자발생부채");
        rowIndexTermMap.putIfAbsent(20, "영업이익률");
        rowIndexTermMap.putIfAbsent(21, "순이익률");
        rowIndexTermMap.putIfAbsent(22, "ROE(%)");
        rowIndexTermMap.putIfAbsent(23, "ROA(%)");
        rowIndexTermMap.putIfAbsent(24, "부채비율");
        rowIndexTermMap.putIfAbsent(25, "자본유보율");
        rowIndexTermMap.putIfAbsent(26, "EPS(원)");
        rowIndexTermMap.putIfAbsent(27, "PER(배)");
        rowIndexTermMap.putIfAbsent(28, "BPS(원)");
        rowIndexTermMap.putIfAbsent(29, "PBR(배)");
        rowIndexTermMap.putIfAbsent(30, "현금DPS(원)");
        rowIndexTermMap.putIfAbsent(31, "현금배당수익률");
        rowIndexTermMap.putIfAbsent(32, "현금배당성향(%)");
        rowIndexTermMap.putIfAbsent(33, "발행주식수(보통주)");
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
//        return "http://companyinfo.stock.naver.com/v1/company/ajax/cF1001.aspx?cmp_cd=900260&fin_typ=0&freq_typ=Y";
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

            // Return empty fsheet here to not mess up downstream logic
            if(StringUtils.isEmpty(title.text())) {
                f.setYear(-1);      // means it's invalid column in HTML
                return f;
            }

            f.setFinancialSheetId(UUID.randomUUID());
            f.setStockId(stock.getStockId());
            f.parseNaverYearQuarterString(peridType, Utils.norm(title.text()));
            f.setFinancialSheetType(sheetType);
            f.setCreatedDate(DateTime.now());
            return f;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void doImport(final Stock stock, FinancialSheetPeridType periodType) {
        final int FSHEET_LIST_NDEX = 1;

        FinancialSheetType sheetType = FinancialSheetType.CORE_INFO;

        Try<Document> fsheet = getFinancialSheet(stock.getStockCode(), periodType);
        if(fsheet.isFailure()) {
            LOGGER.error(String.format("Getting financial sheets for %s has failed", stock.getStockName()));
            LOGGER.error(fsheet.getCause().getMessage());
            return;
        }

        Elements rows = fsheet.get().select("tr");
        if(rows.size() != rowIndexTermMap.size()) {
            LOGGER.error(String.format("Predefined format and actual HTML doesn't match. Fix parsing logic. stockCode=%s, stockName=%s", stock.getStockCode(), stock.getStockName()));
            return;
//            throw new HtmlParseException(String.format("Predefined format and actual HTML doesn't match. Fix parsing logic. stockCode=%s, stockName=%s", stock.getStockCode(), stock.getStockName()));
        }

        // Parse list of financial sheets
        List<FinancialSheet> parsedFSheets = parseFSheetListRow(sheetType, periodType, stock, rows.get(FSHEET_LIST_NDEX));
        List<FinancialSheet> storedFSheets = marketRepository.getFinancialSheetsByStock(stock.getStockId());

        for(FinancialSheet parsedFSheet : parsedFSheets) {
            if(parsedFSheet.isValidFinancialSheet() && !storedFSheets.contains(parsedFSheet)) {
                marketRepository.insertFinancialSheet(parsedFSheet);
                storedFSheets.add(parsedFSheet);
            }
        }

        for(int rowIdx = 2; rowIdx <= rowIndexTermMap.size() - 1; rowIdx++) {
            String pageDefinedTerm = rowIndexTermMap.get(rowIdx);
            String parsedTermName = Utils.norm(rows.get(rowIdx).select("th").text());
            String standardTerm;
            if(!pageDefinedTerm.equals(parsedTermName)) {
                throw new HtmlParseException("Format error: expected=" + pageDefinedTerm + " ,actual=" + parsedTermName);
            } else {
                standardTerm = marketRepository.getStandarTerm(parsedTermName);
            }

            Elements values = rows.get(rowIdx).select("td.num");
            for(int elemIdx = 0; elemIdx < parsedFSheets.size(); elemIdx++) {
                String value = values.get(elemIdx).text();
                if(StringUtils.isEmpty(value)) continue;

                FinancialSheet processingFSheet = parsedFSheets.get(elemIdx);
                FinancialSheet curFSheet = storedFSheets.stream().filter(fs -> fs.equals(processingFSheet)).findFirst().get();

                FinancialSheetItem item = new FinancialSheetItem(standardTerm, new Double(values.get(elemIdx).text().replace(",","")), DateTime.now());
                marketRepository.saveFinancialSheetItem(curFSheet.getFinancialSheetId(), item);
            }
        }
        LOGGER.trace("Crawling financial sheets for " + stock.getStockName() + " is done");
    }

    public void run() {
        marketRepository.getMarkets().stream().forEach(m -> {
            m.getStocks().parallelStream().forEach(s -> {
                doImport(s, FinancialSheetPeridType.QUARTERLY);
                doImport(s, FinancialSheetPeridType.ANNUAL);
            });
        });
    }

}
