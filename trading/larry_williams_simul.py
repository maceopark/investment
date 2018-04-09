
import const
from datetime import datetime
from Oracle import Connection

# create new subclassed connection and cursor
connection = Connection(const.CONNECT_STRING)

def get_stock_daily(stock_code, date, ma=5):
    cursor = connection.cursor()
    sql = """
    select yyyymmdd,
        start_price as open,
        high_price as high,
        low_price as low,
        final_price as close,
        ma
    from (
        select yyyymmdd, 
                start_price, 
                high_price, 
                low_price, 
                final_price, 
                avg(final_price) over (order by yyyymmdd rows between :ma-1 preceding and current row) as ma
        from datacrawl.stock_daily_data
        where stock_id = (select stock_id from datacrawl.stock where stock_code = :stock_code)
        and yyyymmdd between :yyyymmdd - (:ma + 7) and :yyyymmdd
    ) a
    where a.yyyymmdd = :yyyymmdd
            """
    param = {'stock_code':stock_code, 'ma':ma, 'yyyymmdd':date}
    cursor.execute(sql, param)
    row = cursor.fetchone()

    return row

sd = get_stock_daily('008560', datetime(2017,12,28), 5)
print(sd)

INITIAL_FUND = 100000000

def make_one_trade(prev_stock_data, today_stock_data):
    pass


connection.close()