declare
  l_cnt number;
begin
  select count(*) into l_cnt from all_tables where table_name = 'QUANT_DATA_STAGING' and owner = 'DATACRAWL';
  
  if l_cnt = 1
  then
    execute immediate 'drop table datacrawl.quant_data_staging';
  end if;
end;
/

  
create table QUANT_DATA_STAGING
as
with q_f as (
select /*+ parallel(4) */ fiscal,
    a.symbol,
    a.company,
    a.report_date,
    a.net_inco,
    a.STKH_EQTY,
    a.sale,
    a.shares,
    a.cur_asst,
    a.liability,
    a.net_inco / a.shares as eps,
    a.STKH_EQTY / a.shares as bps,
    a.sale / a.shares as sps
from goldenfishery.finance a
--where symbol = '005930' -- samsung elec
) , b as (
select row_number() over (partition by symbol, fiscal order by yyyymmdd) as rn, 
        a.* , 
        yyyymmdd, 
        final_price, 
        lag(final_price,4) over (partition by company order by fiscal) as final_price_1year_ago,
        round(final_price / (eps+0.0001), 1) as per, 
        round(final_price / (bps+0.0001), 1) as pbr, 
        round(final_price / (sps+0.0001), 1) as psr,
        final_price * shares as market_cap,
        case when net_inco > 0 then 1 else 0 end plus_net_inco,
        (select round(avg(int_rate),2) from datacrawl.interest_rate where market_id = 'KOSPI' and int_rate_type='KR_TREASURE_BOND_3Y' and yyyymmdd between a.report_date-4 and a.report_date) as kr_3ybond_int_rate,
        case when (cur_asst - liability) * 0.85 - (final_price * shares) > 0 then 1 else 0 end ncav,
        (select round(avg(final_price))
           from datacrawl.stock_daily_data
          where stock_id = '44ab5d82-0501-4d6d-8ac5-320895c5c1e2'
            and yyyymmdd between add_months(a.report_date, -3) and a.report_date) as kodex200ma60,
        (select round(avg(final_price))
        from stock s 
        inner join STOCK_DAILY_DATA sd on sd.stock_id = s.stock_id
        where yyyymmdd between add_months(report_date,-3) and add_months(report_date,-3) + 5
        and stock_code = symbol) as prev_q_price,
        (select round(avg(final_price))
        from stock s 
        inner join STOCK_DAILY_DATA sd on sd.stock_id = s.stock_id
        where yyyymmdd between add_months(report_date,3) and add_months(report_date,3) + 5
        and stock_code = symbol) as next_q_price
from q_f a
inner join stock s on a.symbol = s.stock_code
inner join STOCK_DAILY_DATA sd on sd.stock_id = s.stock_id
and yyyymmdd between a.report_date and a.report_date + 4
)
select symbol, 
    company, 
    fiscal,
    report_date,    
    per, 
    pbr, 
    psr,
    kr_3ybond_int_rate,    
    plus_net_inco,
    ncav,
    kodex200ma60,
    case when percent_rank() over (partition by fiscal order by market_cap desc) < 0.2 then 1 -- big sized
        when  percent_rank() over (partition by fiscal order by market_cap desc) between 0.2 and 0.8 then 2  -- medium sized
        else 3 end as stock_class, -- small sized
    market_cap,
    case when (final_price - final_price_1year_ago) / final_price_1year_ago*100 >= 30 then 1 else 0 end as yoy_over_30pct,
    case when (final_price - prev_q_price)/prev_q_price*100 >= 10 then 1 else 0 end as qoq_over_10pct,
    case when (next_q_price - final_price)/final_price*100 >= 10 then 1 else 0 end as next_q_over_10pct
--    net_inco,
--    STKH_EQTY,
--    sale,
--    shares,
--    round(eps), 
--    round(bps), 
--    round(sps),
--    market_cap
from b
where rn = 1
order by symbol, fiscal;


--
--
--select c.stock_code, c.stock_name, b.year, b.quarter, a.term, a.value
--from FINANCIAL_SHEET_ITEM a
--inner join financial_sheet b on a.FINANCIAL_SHEET_ID = b.financial_sheet_id
--inner join stock c on b.stock_id = c.stock_id
--where term in ('????????')
--and quarter > 0
--order by stock_Code, year, quarter, term;
--
--
--
--select c.stock_code, c.stock_name, b.year, b.quarter, a.term, a.value
--from FINANCIAL_SHEET_ITEM a
--inner join financial_sheet b on a.FINANCIAL_SHEET_ID = b.financial_sheet_id
--inner join stock c on b.stock_id = c.stock_id
--where term in ('???')
--and quarter > 0
--order by stock_Code, year, quarter desc

select * from (
    select  --symbol,
            fiscal||'_'||company as idx,
            per,pbr,psr,kr_3ybond_int_rate,plus_net_inco,ncav,kodex200ma60,stock_class,yoy_over_30pct,qoq_over_10pct,
            
            lead(per,1) over(partition by symbol order by fiscal) as per_nq1, 
            lead(pbr,1) over(partition by symbol order by fiscal) as pbr_nq1,
            lead(psr,1) over(partition by symbol order by fiscal) as psr_nq1,
            lead(kr_3ybond_int_rate,1) over(partition by symbol order by fiscal) as kr_3ybond_int_rate_nq1,
            lead(plus_net_inco,1) over(partition by symbol order by fiscal) as plus_net_inco_nq1,
            lead(ncav,1) over(partition by symbol order by fiscal) as ncav_nq1,
            lead(kodex200ma60,1) over(partition by symbol order by fiscal) as kodex200ma60_nq1,
            lead(stock_class,1) over(partition by symbol order by fiscal) as stock_class_nq1,
            lead(yoy_over_30pct,1) over(partition by symbol order by fiscal) as yoy_over_30pct_nq1,
            lead(qoq_over_10pct,1) over(partition by symbol order by fiscal) as qoq_over_10pct_nq1,
            
            lead(per,2) over(partition by symbol order by fiscal) as per_nq2, 
            lead(pbr,2) over(partition by symbol order by fiscal) as pbr_nq2,
            lead(psr,2) over(partition by symbol order by fiscal) as psr_nq2,
            lead(kr_3ybond_int_rate,2) over(partition by symbol order by fiscal) as kr_3ybond_int_rate_nq2,
            lead(plus_net_inco,2) over(partition by symbol order by fiscal) as plus_net_inco_nq2,
            lead(ncav,2) over(partition by symbol order by fiscal) as ncav_nq2,
            lead(kodex200ma60,2) over(partition by symbol order by fiscal) as kodex200ma60_nq2,
            lead(stock_class,2) over(partition by symbol order by fiscal) as stock_class_nq2,
            lead(yoy_over_30pct,2) over(partition by symbol order by fiscal) as yoy_over_30pct_nq2,
            lead(qoq_over_10pct,2) over(partition by symbol order by fiscal) as qoq_over_10pct_nq2,
            
            lead(per,3) over(partition by symbol order by fiscal) as per_nq3, 
            lead(pbr,3) over(partition by symbol order by fiscal) as pbr_nq3,
            lead(psr,3) over(partition by symbol order by fiscal) as psr_nq3,
            lead(kr_3ybond_int_rate,3) over(partition by symbol order by fiscal) as kr_3ybond_int_rate_nq3,
            lead(plus_net_inco,3) over(partition by symbol order by fiscal) as plus_net_inco_nq3,
            lead(ncav,3) over(partition by symbol order by fiscal) as ncav_nq3,
            lead(kodex200ma60,3) over(partition by symbol order by fiscal) as kodex200ma60_nq3,
            lead(stock_class,3) over(partition by symbol order by fiscal) as stock_class_nq3,
            lead(yoy_over_30pct,3) over(partition by symbol order by fiscal) as yoy_over_30pct_nq3,
            lead(qoq_over_10pct,3) over(partition by symbol order by fiscal) as qoq_over_10pct_nq3,

            lead(per,4) over(partition by symbol order by fiscal) as per_nq4, 
            lead(pbr,4) over(partition by symbol order by fiscal) as pbr_nq4,
            lead(psr,4) over(partition by symbol order by fiscal) as psr_nq4,
            lead(kr_3ybond_int_rate,4) over(partition by symbol order by fiscal) as kr_3ybond_int_rate_nq4,
            lead(plus_net_inco,4) over(partition by symbol order by fiscal) as plus_net_inco_nq4,
            lead(ncav,4) over(partition by symbol order by fiscal) as ncav_nq4,
            lead(kodex200ma60,4) over(partition by symbol order by fiscal) as kodex200ma60_nq4,
            lead(stock_class,4) over(partition by symbol order by fiscal) as stock_class_nq4,
            lead(yoy_over_30pct,4) over(partition by symbol order by fiscal) as yoy_over_30pct_nq4,
            lead(qoq_over_10pct,4) over(partition by symbol order by fiscal) as qoq_over_10pct_nq4,

            lead(per,5) over(partition by symbol order by fiscal) as per_nq5, 
            lead(pbr,5) over(partition by symbol order by fiscal) as pbr_nq5,
            lead(psr,5) over(partition by symbol order by fiscal) as psr_nq5,
            lead(kr_3ybond_int_rate,5) over(partition by symbol order by fiscal) as kr_3ybond_int_rate_nq5,
            lead(plus_net_inco,5) over(partition by symbol order by fiscal) as plus_net_inco_nq5,
            lead(ncav,5) over(partition by symbol order by fiscal) as ncav_nq5,
            lead(kodex200ma60,5) over(partition by symbol order by fiscal) as kodex200ma60_nq5,
            lead(stock_class,5) over(partition by symbol order by fiscal) as stock_class_nq5,
            lead(yoy_over_30pct,5) over(partition by symbol order by fiscal) as yoy_over_30pct_nq5,
            lead(qoq_over_10pct,5) over(partition by symbol order by fiscal) as qoq_over_10pct_nq5,

            lead(per,6) over(partition by symbol order by fiscal) as per_nq6, 
            lead(pbr,6) over(partition by symbol order by fiscal) as pbr_nq6,
            lead(psr,6) over(partition by symbol order by fiscal) as psr_nq6,
            lead(kr_3ybond_int_rate,6) over(partition by symbol order by fiscal) as kr_3ybond_int_rate_nq6,
            lead(plus_net_inco,6) over(partition by symbol order by fiscal) as plus_net_inco_nq6,
            lead(ncav,6) over(partition by symbol order by fiscal) as ncav_nq6,
            lead(kodex200ma60,6) over(partition by symbol order by fiscal) as kodex200ma60_nq6,
            lead(stock_class,6) over(partition by symbol order by fiscal) as stock_class_nq6,
            lead(yoy_over_30pct,6) over(partition by symbol order by fiscal) as yoy_over_30pct_nq6,
            lead(qoq_over_10pct,6) over(partition by symbol order by fiscal) as qoq_over_10pct_nq6,

            lead(per,7) over(partition by symbol order by fiscal) as per_nq7, 
            lead(pbr,7) over(partition by symbol order by fiscal) as pbr_nq7,
            lead(psr,7) over(partition by symbol order by fiscal) as psr_nq7,
            lead(kr_3ybond_int_rate,7) over(partition by symbol order by fiscal) as kr_3ybond_int_rate_nq7,
            lead(plus_net_inco,7) over(partition by symbol order by fiscal) as plus_net_inco_nq7,
            lead(ncav,7) over(partition by symbol order by fiscal) as ncav_n73,
            lead(kodex200ma60,7) over(partition by symbol order by fiscal) as kodex200ma60_nq7,
            lead(stock_class,7) over(partition by symbol order by fiscal) as stock_class_nq7,
            lead(yoy_over_30pct,7) over(partition by symbol order by fiscal) as yoy_over_30pct_nq7,
            lead(qoq_over_10pct,7) over(partition by symbol order by fiscal) as qoq_over_10pct_nq7,
            
            lead(next_q_over_10pct,8) over(partition by symbol order by fiscal) as next_q_over_10pct_nq8 
    from quant_data_staging
) a
where 1=1
and per between -300 and 300 
and per_nq1 between -300 and 300 
and per_nq2 between -300 and 300 
and per_nq3 between -300 and 300 
and per_nq4 between -300 and 300 
and per_nq5 between -300 and 300 
and per_nq6 between -300 and 300 
and per_nq7 between -300 and 300 
and pbr >= 0.2
;
--order by symbol, fiscal;


