-- 발행주식수
select s.STOCK_NAME, f.YEAR, f.QUARTER, fi.value
from stock s
inner join financial_sheet f on s.STOCK_ID = f.STOCK_ID
inner join financial_sheet_item fi on f.FINANCIAL_SHEET_ID = fi.FINANCIAL_SHEET_ID
where fi.term = '발행주식수'
and quarter <> 0
order by stock_name, year, quarter;

