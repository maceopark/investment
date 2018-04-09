alter session set workarea_size_policy=manual;
alter session set hash_area_size=1024000000;
alter session set sort_area_size=1024000000;

drop table LW_SIMUL_STAGING;

create table LW_SIMUL_STAGING
nologging
as
with sd as (
  select /*+ parallel(4) */ a.yyyymmdd
         , s.stock_name
         , s.stock_code
         , a.final_price as close
         , a.start_price as open
         , a.high_price as high
         , a.low_price as low
         , avg(final_price) over (partition by a.stock_id order by yyyymmdd rows between 4 preceding and current row) as ma5
         , lag(final_price,1) over (partition by a.stock_id order by yyyymmdd) as prev_close
         , lag(start_price,1) over (partition by a.stock_id order by yyyymmdd) as prev_open
         , lag(high_price, 1) over (partition by a.stock_id order by yyyymmdd) as prev_high
         , lag(low_price, 1) over (partition by a.stock_id order by yyyymmdd) as prev_low
         , min(to_char(yyyymmdd,'YYYY/MM/DD') || '_' || final_price) over (partition by a.stock_id, extract(year from yyyymmdd)) as year_start_price
  from datacrawl.stock_daily_data a
  inner join stock s on a.stock_id = s.stock_id
  where 1=1
  and stock_code in('005935')
  and yyyymmdd between sysdate-365*10 and sysdate
  AND (a.trade_volume * a.final_price) > 100000000 -- 1억 거래대금 이상
)
select yyyymmdd
       ,stock_name
       ,stock_code
       ,ma5
       ,get_lr_coeff
       ,high
       ,get_lr_purchase_indicator(open, prev_high, prev_low) as purchase_price
       ,close
       ,case when high > get_lr_purchase_indicator(open, prev_high, prev_low) and get_lr_purchase_indicator(open, prev_high, prev_low) > ma5
              then get_lr_daily_profit_per_share(close, open, prev_high, prev_low) else 0 end as daily_profit_per_share
       ,to_number(substr(year_start_price,12)) as year_start_price
       ,to_number(substr(year_start_price,0,4)) as year
       ,100000000 as fund
from sd;

select * 
from lw_simul_staging
order by stock_code, yyyymmdd;

declare
  l_fund number := 100000000;
begin
  for i in (select * from lw_simul_staging where purchase_price is not null order by stock_code, yyyymmdd)
  loop
    l_fund := l_fund + round(l_fund / i.purchase_price) * i.daily_profit_per_share;
    dbms_output.put_line(i.yyyymmdd || '=' || l_fund);
  end loop;
end;



) , yearly_perf as (
select stock_name , stock_code
       , year
       , 1 + sum(daily_profit_per_share) / avg(year_start_price) as yearly_perf 
from daily_perf df
group by stock_name, stock_code, year
) , yearly_perf_ordered as (
select stock_name, stock_code , year , yearly_perf, row_number() over (partition by year order by yearly_perf desc) as rk
from yearly_perf
)
select *
from yearly_perf_ordered
where rk <= 300
--0.5, 520
--0.4, 1443
--select * from datacrawl.stock 
