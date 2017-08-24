package com.maceo.investment.datacrawler.model;

public enum FinancialSheetType {

    CORE_INFO(0),
    BALANCE_SHEET(1),
    CASH_FLOW(2),
    INCOME(3);

    private int code;

    FinancialSheetType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
