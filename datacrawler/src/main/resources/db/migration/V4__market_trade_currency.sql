
alter table datacrawl.market add column TRADE_CURRENCY varchar(3);

update datacrawl.market a set trade_currency = 'KRW' where a.MARKET_ID = 'KOSPI';
update datacrawl.market a set trade_currency = 'KRW' where a.MARKET_ID = 'KOSDAQ';
commit;


