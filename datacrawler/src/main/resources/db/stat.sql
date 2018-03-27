-- 발행주식수
SELECT s.STOCK_NAME, f.YEAR, f.QUARTER, fi.value
FROM stock s
INNER JOIN financial_sheet f ON s.STOCK_ID = f.STOCK_ID
INNER JOIN financial_sheet_item fi ON f.FINANCIAL_SHEET_ID = fi.FINANCIAL_SHEET_ID
WHERE fi.term = '발행주식수'
AND QUARTER <> 0
ORDER BY stock_name, YEAR, QUARTER;

SELECT s.STOCK_NAME, CONCAT(f.YEAR, '/' , f.QUARTER), fi.term, fi.value
FROM datacrawl.stock s
INNER JOIN financial_sheet f ON s.stock_id = f.stock_id
INNER JOIN financial_sheet_item fi ON f.FINANCIAL_SHEET_ID = fi.FINANCIAL_SHEET_ID
WHERE stock_code = '005930'
AND market_id = 'KOSPI'
AND QUARTER <> 0
AND f.estimated_sheet = 0
ORDER BY YEAR DESC, QUARTER DESC;

SELECT *
FROM datacrawl.stock_daily_data sdd
INNER JOIN datacrawl.stock s ON sdd.stock_id = s.stock_id
WHERE sdd.stock_id = '06fb851a-77ea-438d-a87d-0e4b6a04cedd'
ORDER BY yyyymmdd DESC;


SELECT s.stock_name
    , sd.yyyymmdd
    , final_price
    , start_price
    , high_price
    , low_price
    , trade_volume
    , (SELECT int_rate FROM datacrawl.interest_rate WHERE yyyymmdd = sd.yyyymmdd AND market_id = m.market_id AND int_rate_type = 'KR_TREASURE_BOND_3Y') AS KR_TREASURE_BOND_3Y
FROM datacrawl.stock_daily_data sd
INNER JOIN datacrawl.stock s ON sd.stock_id = s.stock_id
INNER JOIN datacrawl.market m ON s.market_id = m.market_id
CROSS JOIN (SELECT @rownum := 0) r
WHERE s.stock_id = '44ab5d82-0501-4d6d-8ac5-320895c5c1e2' -- KODEX 200
ORDER BY yyyymmdd
