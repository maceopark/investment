package com.maceo.investment.datacrawler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

public class Utils {

    public static String yyyymmdd(DateTime dateTime) {
        return DateTimeFormat.forPattern("yyyyMMdd").print(dateTime);
    }

    public static DateTime utcNow() {
        return DateTime.now().withZone(DateTimeZone.UTC);
    }

    public static DateTime krNowYYYYMMDD() {
        return DateTime.now().withZone(DateTimeZone.forID("Asia/Seoul")).dayOfMonth().roundFloorCopy();
    }

    public static DateTime yyyymmdd(String yyyymmdd) {
        return DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(yyyymmdd);
    }

    public static String norm(String s) {
        return s.replace("\u00a0"," ") //&nbsp is converted to "\u00a0" by Jsoup.
                .replace(".", "")
                .replace(" ", "")
                .replace(",", "")
                .replaceAll("\\s", "");
    }

    public static void randomSleep(long maxSeconds) throws InterruptedException {
        Thread.sleep((long)(Math.random() * maxSeconds * 1000));
    }
}
