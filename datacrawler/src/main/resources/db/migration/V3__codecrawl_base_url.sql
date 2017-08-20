
alter table datacrawl.market add column code_crawl_base_url varchar(200);

update datacrawl.market a set code_crawl_base_url = 'http://finance.naver.com/sise/sise_market_sum.nhn?sosok=0&page=#{pageNum}' where a.MARKET_ID = 'KOSPI';
update datacrawl.market a set code_crawl_base_url = 'http://finance.naver.com/sise/sise_market_sum.nhn?sosok=1&page=#{pageNum}' where a.MARKET_ID = 'KOSDAQ';
commit;

