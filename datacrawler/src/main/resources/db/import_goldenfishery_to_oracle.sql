drop table goldenfishery.finance;

create table goldenfishery.FINANCE
as
select "finance_id" as finance_id,
  cast("country" as VARCHAR2(3)) as country,
  cast("fiscal" as VARCHAR2(6)) as fiscal,
  cast("symbol" as VARCHAR2(128)) as symbol,
  cast("company" as VARCHAR2(128)) as company,
  cast("market" as VARCHAR2(45)) as market,
  "sale" as sale,
  "cost_sales" as cost_sales,
  "sell_adm_expns" as sell_adm_expns,
  "oper_inco" as oper_inco,
  "net_inco" as net_inco,
  "liability" as liability,
  "cur_liab" as cur_liab,
  "cur_asst" as cur_asst,
  "asst" as asst,
  "stkh_eqty" as stkh_eqty,
  "shares" as shares,
  "shares_all" as shares_all,
  "report_date" as report_date,
  "yr_final" as yr_final,
  "yr_adjusted" as yr_adjusted,
  "sale_acumu" as sale_acumu,
  "oper_inco_acumu" as oper_inco_acumu,
  "net_inco_acumu" as net_inco_acumu
from godenfishery.finance@goldenfishery
nologging;

drop table goldenfishery.stock;

create table goldenfishery.stock
as
select "stock_id" as stock_id,
        cast("country" as varchar2(3)) as country,
        cast("company" as varchar2(128)) as company,
        cast("symbol" as varchar2(128)) as symbol,
        cast("market" as varchar2(45)) as market,
        "lastupdate" as lastupdate,
        "lasttradedate" as lasttradedate,
        cast("symbol_yahoo" as varchar2(128)) as symbol_yahoo,
        cast("symbol_google" as varchar2(128)) as symbol_google,
        "ban" as ban,
        "sector" as sector,
        "business" as business
from goldenfishery.stock@goldenfishery
nologging;

drop table goldenfishery.stock_history;

create table goldenfishery.stock_history
as
select "stock_history_id" as stock_history_id,
        "stock_id" as stock_id,
        "date" as yyyymmdd,
        "open" as open_price,
        "close" as close_price,
        "high" as high_price,
        "low" as low_price,
        "volume" as volume
from goldenfishery.stock_history@goldenfishery
nologging;


grant select on goldenfishery.stock to datacrawl;
grant select on goldenfishery.stock_history to datacrawl;
grant select on goldenfishery.finance to datacrawl;

