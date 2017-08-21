package com.maceo.investment.datacrawler.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

public class DateTimeUtils {

    public static String yyyymmdd(DateTime dateTime) {
        return DateTimeFormat.forPattern("yyyyMMdd").print(dateTime);
    }

    public static DateTime utcNow() {
        return DateTime.now().withZone(DateTimeZone.UTC);
    }
}
