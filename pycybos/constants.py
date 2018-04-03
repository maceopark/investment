MARKET_KOSPI = 1
MARKET_KOSDAQ = 2
MARKET_ID_MAP = {1:"KOSPI", 2:"KOSDAQ"}

SUPERVISION_NONE = 0        # 일반종목
UNDER_SUPERVISION = 1      # 관리종목
SUPERVISION_MAP = {0:"일반종목", 1:"관리종목"}

OCX_CPUTIL_CODE_MANAGER = "CpUtil.CpCodeMgr"
OCX_CPUTIL_CYBOS = "CpUtil.CpCybos"


OCX_STOCK_CHART = "CpSysDib.StockChart"
INDEX_STOCK_CODE = 0
INDEX_REQUEST_TYPE = 1
INDEX_REQUEST_TYPE_PERIOD = '1'
INDEX_REQUEST_TYPE_COUNT = '2'
INDEX_START_DATE = 2 # long, yyyymmdd default:0 (최근거래날짜)
INDEX_END_DATE = 3 # long, yyyymmdd
INDEX_COUNT = 4 # 요청할 데이터 갯수
INDEX_DATA = 5 # objStockChart.SetInputValue(5, [0,2,3,4,5, 8]) #날짜,시가,고가,저가,종가,거래량
#
# 0: 날짜(ulong)
# 1:시간(long) - hhmm
# 2:시가(long or float)
# 3:고가(long or float)
# 4:저가(long or float)
# 5:종가(long or float)
# 6:전일대비(long or float) - 주) 대비부호(37)과반드시같이요청해야함
# 8:거래량(ulong or ulonglong)주) 정밀도만원단위
# 9:거래대금(ulonglong)
# 10:누적체결매도수량(ulong or ulonglong) -호가비교방식누적체결매도수량
# 11:누적체결매수수량(ulong or ulonglong) -호가비교방식누적체결매수수량   #  (주) 10, 11 필드는분,틱요청일때만제공
# 12:상장주식수(ulonglong)
# 13:시가총액(ulonglong)
# 14:외국인주문한도수량(ulong)
# 15:외국인주문가능수량(ulong)
# 16:외국인현보유수량(ulong)
# 17:외국인현보유비율(float)
# 18:수정주가일자(ulong) - YYYYMMDD
# 19:수정주가비율(float)
# 20:기관순매수(long)
# 21:기관누적순매수(long)
# 22:등락주선(long)
# 23:등락비율(float)
# 24:예탁금(ulonglong)
# 25:주식회전율(float)
# 26:거래성립률(float)
# 37:대비부호(char) - 수신값은 GetHeaderValue 8 대비부호와동일

INDEX_CHART_TYPE = 6    # objStockChart.SetInputValue(6, ord('D')) # '차트 주가 - 일간 차트 요청
# 'D'	일       # 'W'	주       # 'M'	월       # 'm'	분       # 'T'	틱

INDEX_JUGI = 7      #default -1
INDEX_GAP_CALIBRATED = 8
# 코드	내용
# '0'	갭무보정 [Default]
# '1'	갭보정

INDEX_CALIBRATED_PRICE = 9  # objStockChart.SetInputValue(9, '1') # 수정주가 사용
# 코드	내용
# '0'	무수정주가 [Default]
# '1'	수정주가

INDEX_TRADE_VOL_GUBUN = 10
# 코드	내용
# '1'	시간외거래량모두포함[Default]
# '2'	장종료시간외거래량만포함
# '3'	시간외거래량모두제외
# '4'	장전시간외거래량만포함






# import win32com.client
#
# # 연결 여부 체크
# objCpCybos = win32com.client.Dispatch("CpUtil.CpCybos")
# bConnect = objCpCybos.IsConnect
# if (bConnect == 0):
#     print("PLUS가 정상적으로 연결되지 않음. ")
#     exit()
#
# # 차트 객체 구하기
# objStockChart = win32com.client.Dispatch("CpSysDib.StockChart")
#
# objStockChart.SetInputValue(0, 'A005930')  # 종목 코드 - 삼성전자
# objStockChart.SetInputValue(1, '2')  # 개수로 조회
# objStockChart.SetInputValue(4, 100)  # 최근 100일 치
# objStockChart.SetInputValue(5, [0, 2, 3, 4, 5, 8])  # 날짜,시가,고가,저가,종가,거래량
# objStockChart.SetInputValue(6, ord('D'))  # '차트 주가 - 일간 차트 요청
# objStockChart.SetInputValue(9, '1')  # 수정주가 사용
# objStockChart.BlockRequest()
#
# len = objStockChart.GetHeaderValue(3)
#
# print("날짜", "시가", "고가", "저가", "종가", "거래량")
# print("빼기빼기==============================================-")
#
# for i in range(len):
#     day = objStockChart.GetDataValue(0, i)
#     open = objStockChart.GetDataValue(1, i)
#     high = objStockChart.GetDataValue(2, i)
#     low = objStockChart.GetDataValue(3, i)
#     close = objStockChart.GetDataValue(4, i)
#     vol = objStockChart.GetDataValue(5, i)
#     print(day, open, high, low, close, vol)
#
