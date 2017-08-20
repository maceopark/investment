drop table if exists datacrawl.stock;
drop table if exists datacrawl.market;

CREATE TABLE `market` (
  `MARKET_ID` varchar(36) NOT NULL,
  `MARKET_DESC` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`MARKET_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `stock` (
  `STOCK_ID` varchar(36) NOT NULL,
  `MARKET_ID` varchar(36) NOT NULL,
  `STOCK_CODE` varchar(45) NOT NULL,
  `STOCK_NAME` varchar(45) NOT NULL,
  PRIMARY KEY (`STOCK_ID`),
  UNIQUE KEY `UQ_MARKET_STOCK` (`STOCK_CODE`,`MARKET_ID`),
  KEY `MARKET_ID` (`MARKET_ID`),
  CONSTRAINT `stock_ibfk_1` FOREIGN KEY (`MARKET_ID`) REFERENCES `market` (`MARKET_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


