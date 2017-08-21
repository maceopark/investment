CREATE TABLE IF NOT EXISTS `datacrawl`.`STOCK_DAILY_DATA` (
  `STOCK_ID` VARCHAR(36) NOT NULL,
  `YYYYMMDD` VARCHAR(8) NOT NULL,
  `FINAL_PRICE` INT NULL COMMENT '종가',
  `PRICE_DIFF` INT NULL COMMENT '전일대비등락',
  `START_PRICE` INT NULL COMMENT '시가',
  `HIGH_PRICE` INT NULL COMMENT '고가',
  `LOW_PRICE` INT NULL COMMENT '저가',
  `TRADE_VOLUME` INT NULL COMMENT '거래량',
  PRIMARY KEY (`STOCK_ID`, `YYYYMMDD`),
  INDEX `fk_STOCK_DAILY_DATA_STOCK1_idx` (`STOCK_ID` ASC),
  CONSTRAINT `fk_STOCK_DAILY_DATA_STOCK1`
    FOREIGN KEY (`STOCK_ID`)
    REFERENCES `datacrawl`.`STOCK` (`STOCK_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
