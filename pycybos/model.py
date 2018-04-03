
class Stock:

    def __init__(self, stock_id, market_id, stock_code, stock_name, industry_name, supervision, fiscal_month):
        self.stock_id = stock_id
        self.market_id = market_id
        self.stock_code = stock_code
        self.stock_name = stock_name
        self.industry_name = industry_name      # 반환값 : 증권전산업종코드 (대신)
        self.supervision = supervision
        self.fiscal_month = fiscal_month
