package com.maceo.investment.datacrawler.model;

import com.maceo.investment.datacrawler.Utils;
import com.maceo.investment.datacrawler.exception.HtmlParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialSheet {
    private UUID financialSheetId;
    private UUID stockId;
    private int year;
    private int quarter;
    private FinancialSheetType financialSheetType;
    private boolean estimatedSheet;
    private DateTime createdDate;

    public void parseNaverYearQuarterString(FinancialSheetPeridType peridType, String yearQuarter) {
        if(yearQuarter.contains("(E)")) {
            this.estimatedSheet = true;
        } else {
            this.estimatedSheet = false;
        }

        this.year = new Integer(yearQuarter.substring(0, 4));

        if(peridType.equals(FinancialSheetPeridType.ANNUAL)) {
            this.quarter = 0;
        } else if(peridType.equals(FinancialSheetPeridType.QUARTERLY)) {
            String month = yearQuarter.substring(5, 7);
            this.quarter = new Integer(month);
        } else {
            throw new HtmlParseException(String.format("%s is invalid year/quarter format", yearQuarter));
        }
    }

    public boolean isValidFinancialSheet() {
        return this.year > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinancialSheet)) return false;

        FinancialSheet that = (FinancialSheet) o;

        if (getYear() != that.getYear()) return false;
        if (getQuarter() != that.getQuarter()) return false;
        if (isEstimatedSheet() != that.isEstimatedSheet()) return false;
        if (!getStockId().equals(that.getStockId())) return false;
        return getFinancialSheetType() == that.getFinancialSheetType();
    }

    @Override
    public int hashCode() {
        int result = getStockId().hashCode();
        result = 31 * result + getYear();
        result = 31 * result + getQuarter();
        result = 31 * result + getFinancialSheetType().hashCode();
        result = 31 * result + (isEstimatedSheet() ? 1 : 0);
        return result;
    }
}
