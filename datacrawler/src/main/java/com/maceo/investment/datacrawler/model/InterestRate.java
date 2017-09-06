package com.maceo.investment.datacrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
@AllArgsConstructor
public class InterestRate {
    private final String marketId;
    private final InterestRateType interestRateType;
    private final DateTime yyyymmdd;
    private final double interestRate;
}
