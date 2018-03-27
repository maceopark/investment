import numpy as np
import os
from sklearn.preprocessing import StandardScaler
from math import sqrt
from scipy.ndimage.interpolation import shift
import matplotlib.pyplot as plt
FILE_NAME = 'kospi200_price.tsv'
DATA_PATH = os.path.join('c:\\', 'git-repo','investment','model','data')

datafile = os.path.join(DATA_PATH, FILE_NAME)
data = np.loadtxt(datafile, dtype=float, delimiter='\t', skiprows=1, usecols=(2,3,4,5,6,7), unpack=True)
#print(data)

final_price_tplus1 = shift(data[0], -1, cval=31605)     # use next day's final price as Y. Fill in same price in the shifted column
#print(data[0])  # final_price
#print(final_price_tplus1)
#print(data[1])  # start_price
#print(data[2])  # high_price
#print(data[3])  # low_price
#print(data[4])  # trade_volume
#print(data[5])  #int_rate_kr_trb_3y

def standardizedArray(arr) :
    #print(arr.shape)
    scaler = StandardScaler()
    arr = arr.reshape(-1,1)
    scaler = scaler.fit(arr)
    return scaler.mean_, sqrt(scaler.var_), scaler.transform(arr)

# X1
final_price_mean, final_price_stddev, final_price_norm = standardizedArray(data[0])
print('Final price Mean: %f, StandardDeviation: %f' % (final_price_mean, final_price_stddev))

# X2
start_price_mean, start_price_stddev, start_price_norm = standardizedArray(data[1])
print('Start price Mean: %f, StandardDeviation: %f' % (start_price_mean, start_price_stddev))

# X3
high_price_mean, high_price_stddev, high_price_norm = standardizedArray(data[2])
print('High price Mean: %f, StandardDeviation: %f' % (high_price_mean, high_price_stddev))

# X4
low_price_mean, low_price_stddev, low_price_norm = standardizedArray(data[3])
print('Low price Mean: %f, StandardDeviation: %f' % (low_price_mean, low_price_stddev))

# X5
trade_volume_mean, trade_volume_stddev, trade_volume_norm = standardizedArray(data[4])
print('Trade volume Mean: %f, StandardDeviation: %f' % (trade_volume_mean, trade_volume_stddev))

# X6
int_rate_kr_trb_3y_mean, int_rate_kr_trb_3y_stddev, int_rate_kr_trb_3y_norm = standardizedArray(data[5])
print('int_rate_kr_trb_3y volume Mean: %f, StandardDeviation: %f' % (int_rate_kr_trb_3y_mean, int_rate_kr_trb_3y_stddev))

# Y
final_price_tplus1_mean, final_price_tplus1_stddev, final_price_tplus1_norm = standardizedArray(final_price_tplus1)
print('Final t+1 price Mean: %f, StandardDeviation: %f' % (final_price_tplus1_mean, final_price_tplus1_stddev))

plt.hist(final_price_norm, bins=100)
plt.ylabel('normalized price')
plt.show()

