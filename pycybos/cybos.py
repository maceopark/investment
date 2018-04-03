import win32com.client
from constants import *
import uuid
from model import *

def get_stocks_by_market(market) :
    code_manager = win32com.client.Dispatch(OCX_CPUTIL_CODE_MANAGER)
    code_list = code_manager.GetStockListByMarket(market)
    stocks = []
    for code in code_list:
        stock_id = uuid.uuid4()
        market_id = MARKET_ID_MAP.get(market)
        stock_code = code
        stock_name = code_manager.CodeToName(code)
        industry_name = code_manager.GetIndustryName(code_manager.GetStockIndustryCode(code))
        supervision = SUPERVISION_MAP.get(code_manager.GetStockSupervisionKind(code))
        fiscal_month = code_manager.GetStockFiscalMonth (code)
        stock = Stock(stock_id, market_id, stock_code, stock_name, industry_name, supervision, fiscal_month)
        stocks.append(stock)

    return stocks


kospi_stocks = get_stocks_by_market(MARKET_KOSPI)  # 거래소
kosdaq_stocks = get_stocks_by_market(MARKET_KOSDAQ)  # 코스닥

print(kosdaq_stocks)
print(kospi_stocks)

def get_stock_daily_data_by_code(stock_code) :
    stock_chart = win32com.client.Dispatch(OCX_STOCK_CHART)


