CREATE TABLE market (
    market_id             VARCHAR2(36) NOT NULL primary key,
    market_desc           VARCHAR2(45),
    code_crawl_base_url   VARCHAR2(200),
    trade_currency        VARCHAR2(3)
)
LOGGING;


---------------------------------------------------------------

CREATE TABLE interest_rate (
    market_id       VARCHAR2(36) NOT NULL,
    int_rate_type   VARCHAR2(20) NOT NULL,
    yyyymmdd        DATE NOT NULL,
    int_rate        NUMBER NOT NULL
)
LOGGING;

ALTER TABLE interest_rate ADD PRIMARY KEY ( market_id,     yyyymmdd,    int_rate_type );

ALTER TABLE interest_rate     ADD CONSTRAINT fk_market1 FOREIGN KEY ( market_id )         REFERENCES market ( market_id )    NOT DEFERRABLE;


---------------------------------------------------------------


CREATE TABLE stock (
    stock_id       VARCHAR2(36) NOT NULL,
    market_id      VARCHAR2(36) NOT NULL,
    stock_code     VARCHAR2(45) NOT NULL,
    stock_name     VARCHAR2(45) NOT NULL,
    created_date   TIMESTAMP DEFAULT current_timestamp NOT NULL
)
LOGGING;

CREATE INDEX market_id ON stock ( market_id ASC ) LOGGING;

ALTER TABLE stock ADD PRIMARY KEY ( stock_id );

ALTER TABLE stock ADD CONSTRAINT uq_market_stock UNIQUE ( stock_code, market_id );

ALTER TABLE stock ADD CONSTRAINT stock_ibfk_1 FOREIGN KEY ( market_id ) REFERENCES market ( market_id ) NOT DEFERRABLE;



---------------------------------------------------------------


CREATE TABLE stock_daily_data (
    stock_id       VARCHAR2(36) NOT NULL,
    yyyymmdd       DATE NOT NULL,
    final_price    INTEGER,
    price_diff     INTEGER,
    start_price    INTEGER,
    high_price     INTEGER,
    low_price      INTEGER,
    trade_volume   INTEGER
)
LOGGING;

COMMENT ON COLUMN stock_daily_data.final_price IS '종가';

COMMENT ON COLUMN stock_daily_data.price_diff IS '전일대비등락';

COMMENT ON COLUMN stock_daily_data.start_price IS '시가';

COMMENT ON COLUMN stock_daily_data.high_price IS '고가';

COMMENT ON COLUMN stock_daily_data.low_price IS '저가';

COMMENT ON COLUMN stock_daily_data.trade_volume IS '거래량';

CREATE INDEX fk_stock_daily_data_stock1_idx ON stock_daily_data ( stock_id ASC ) LOGGING;

ALTER TABLE stock_daily_data ADD PRIMARY KEY ( stock_id, yyyymmdd );

ALTER TABLE stock_daily_data
    ADD CONSTRAINT fk_stock_daily_data_stock1 FOREIGN KEY ( stock_id )
        REFERENCES stock ( stock_id )
    NOT DEFERRABLE;

-------------------------------------------------------------------------



CREATE TABLE financial_sheet (
    financial_sheet_id     VARCHAR2(36) NOT NULL,
    stock_id               VARCHAR2(36) NOT NULL,
    year                   SMALLINT NOT NULL,
    quarter                SMALLINT NOT NULL,
    financial_sheet_type   VARCHAR2(20),
    estimated_sheet        SMALLINT NOT NULL,
    created_date           TIMESTAMP DEFAULT current_timestamp NOT NULL
)
LOGGING;

COMMENT ON COLUMN financial_sheet.quarter IS '0 : 연간 1,2,3,4 : 분기';

COMMENT ON COLUMN financial_sheet.estimated_sheet IS '추정재무제표여부';

CREATE INDEX fk_financial_sheet_stock1_idx ON
    financial_sheet ( stock_id ASC )
        LOGGING;

ALTER TABLE financial_sheet ADD CONSTRAINT primary PRIMARY KEY ( financial_sheet_id );

ALTER TABLE financial_sheet
    ADD CONSTRAINT fk_financial_sheet_stock1 FOREIGN KEY ( stock_id )
        REFERENCES stock ( stock_id )
    NOT DEFERRABLE;



-------------------------------------------------------------------------

CREATE TABLE financial_sheet_term (
    term   VARCHAR2(36) NOT NULL
)
LOGGING;

ALTER TABLE financial_sheet_term ADD PRIMARY KEY ( term );

-------------------------------------------------------------------------

CREATE TABLE term_variation (
    term        VARCHAR2(36) NOT NULL,
    variation   VARCHAR2(45) NOT NULL
)
LOGGING;

ALTER TABLE term_variation ADD PRIMARY KEY ( term, variation );


ALTER TABLE term_variation ADD CONSTRAINT fk_financial_sheet_term1 FOREIGN KEY ( term )
        REFERENCES financial_sheet_term ( term )
    NOT DEFERRABLE;


-------------------------------------------------------------------------


CREATE TABLE financial_sheet_item (
    financial_sheet_id   VARCHAR2(36) NOT NULL,
    term                 VARCHAR2(36) NOT NULL,
    value                NUMBER,
    created_date         TIMESTAMP DEFAULT current_timestamp NOT NULL
)
LOGGING;


CREATE INDEX fk_financial_sheet_term2_idx ON
    financial_sheet_item ( term ASC )
        LOGGING;

ALTER TABLE financial_sheet_item ADD PRIMARY KEY ( financial_sheet_id, term );


ALTER TABLE financial_sheet_item
    ADD CONSTRAINT fk_term FOREIGN KEY ( term )
        REFERENCES financial_sheet_term ( term )
    NOT DEFERRABLE;


ALTER TABLE financial_sheet_item
    ADD CONSTRAINT fk_financial_sheet_id FOREIGN KEY ( financial_sheet_id )
        REFERENCES financial_sheet ( financial_sheet_id )
    NOT DEFERRABLE;

