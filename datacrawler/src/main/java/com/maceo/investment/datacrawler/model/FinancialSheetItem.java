package com.maceo.investment.datacrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
@AllArgsConstructor
public class FinancialSheetItem {
    private final String term;
    private final double value;
    private final DateTime createdDate;
}
