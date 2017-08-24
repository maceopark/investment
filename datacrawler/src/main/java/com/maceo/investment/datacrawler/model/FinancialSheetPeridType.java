package com.maceo.investment.datacrawler.model;

public enum FinancialSheetPeridType {

    ANNUAL("Y"),
    QUARTERLY("Q");

    private String naverURLqueryType;

    FinancialSheetPeridType(String naverURLqueryType) {
        this.naverURLqueryType = naverURLqueryType;
    }

    public String getNaverURLqueryType() {
        return naverURLqueryType;
    }
}
