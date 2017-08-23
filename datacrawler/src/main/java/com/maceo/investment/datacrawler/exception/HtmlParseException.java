package com.maceo.investment.datacrawler.exception;

public class HtmlParseException extends RuntimeException {

    public HtmlParseException(String message) {
        super(message);
    }

    public HtmlParseException(Throwable e) {
        super(e);
    }
}
