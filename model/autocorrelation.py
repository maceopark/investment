from datetime import datetime
import pandas as pd
import os
import matplotlib.pyplot as plt



# select sd.yyyymmdd||','||sd.final_price||','||(sd.final_price - lag(sd.final_price,1) over (order by yyyymmdd)) / lag(sd.final_price,1) over (order by yyyymmdd) * 100
# from stock_daily_data sd
# inner join stock s on sd.stock_id = s.stock_id
# where s.stock_name = 'KODEX 200'

# read the data
dataset = pd.read_csv(os.path.join('data','bitcoin_all_time.txt'), sep='\t')
dataset["Close_lagged"] = dataset.Close.shift(1)
dataset["Daily_Return"] = (dataset["Close"] - dataset["Close_lagged"])/dataset["Close_lagged"]*100
print(dataset.head(5))
date = pd.to_datetime(dataset.values[:, 0])
daily_return = pd.Series(dataset.values[:,8]).rolling(window=365, center=False).apply(lambda x: pd.Series(x).autocorr())
price = dataset.values[:,4]
# print(daily_return)

fig, ax = plt.subplots(2, sharex=True)
ax[0].plot(price)
ax[0].grid()
ax[1].plot(daily_return)
ax[1].grid()

plt.show()


# def autocorr(x):
#     result = np.correlate(x, x, mode = 'full')
#     maxcorr = np.argmax(result)
#     # print 'maximum = ', result[maxcorr]
#     result = result / result[maxcorr]
#     #
#     return result[int(result.size/2):]
#
# result = autocorr(daily_return)
#
# plt.plot(result, label='autocorr')
# plt.legend()
# plt.show()

#
# # generate some data
# x = np.arange(0.,6.12,0.01)
# y = np.sin(x)
# # y = np.random.uniform(size=300)
# yunbiased = y-np.mean(y)
# ynorm = np.sum(yunbiased**2)
# acor = np.correlate(yunbiased, yunbiased, "same")/ynorm
# # use only second half
# acor = acor[int(len(acor)/2):]
#
# plt.plot(acor)
# plt.plot(y)
# plt.show()