create table rate_centered
as
with data as (
select company, 
       fiscal,
       report_date,
       final_price * shares as market_cap,
       (select avg(final_price)
          from datacrawl.stock_daily_data sd 
         inner join datacrawl.stock s on sd.stock_id = s.stock_id
         where s.stock_code = f.symbol
           and sd.yyyymmdd between f.report_date and f.report_date+5) as report_date_price,
       (select avg(final_price)
          from datacrawl.stock_daily_data sd 
         inner join datacrawl.stock s on sd.stock_id = s.stock_id
         where s.stock_code = f.symbol
           and sd.yyyymmdd between add_months(f.report_date,3) and add_months(f.report_date,3)+5) as report_date_price_nq,           
       oper_inco, 
       lag(oper_inco,4) over (partition by company order by fiscal) as oper_inco_prev_y
from goldenfishery.finance f
--where symbol = '000040'
)
select a.* ,
       round((oper_inco - oper_inco_prev_y)/oper_inco_prev_y*100,1) as oper_inco_yoy_rate,
       round((report_date_price_nq - report_date_price)/report_date_price*100,1) as next_q_price_rate
from data a

select *
from rate_centered
where oper_inco_yoy_rate is not null
and next_q_price_rate is not null;
