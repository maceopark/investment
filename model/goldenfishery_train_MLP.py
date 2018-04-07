import pandas as pd
import os
import matplotlib.pyplot as plt
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error
from keras.models import Sequential
from keras.layers import Dense, LSTM, Bidirectional, TimeDistributed
from math import sqrt
from numpy import concatenate
from keras import optimizers
from keras.layers.normalization import BatchNormalization
from keras.layers import Activation

FILE_NAME = 'goldenfishery.csv'
DATA_PATH = os.path.join('c:\\', 'git-repo','investment','model','data')
FILE_FULL_PATH = os.path.join(DATA_PATH, FILE_NAME)

# fiscall_company column is index column
dataset = pd.read_csv(FILE_FULL_PATH, header=0, index_col=0)
# manually specify column names
dataset.columns = ['PER','PBR','PSR','KR_3YBOND_INT_RATE','PLUS_NET_INCO','NCAV','KODEX200MA60','STOCK_CLASS','YOY_OVER_30PCT','QOQ_OVER_10PCT',
                   'PER_NQ1','PBR_NQ1','PSR_NQ1','KR_3YBOND_INT_RATE_NQ1','PLUS_NET_INCO_NQ1','NCAV_NQ1','KODEX200MA60_NQ1','STOCK_CLASS_NQ1','YOY_OVER_30PCT_NQ1','QOQ_OVER_10PCT_NQ1',
                   'PER_NQ2','PBR_NQ2','PSR_NQ2','KR_3YBOND_INT_RATE_NQ2','PLUS_NET_INCO_NQ2','NCAV_NQ2','KODEX200MA60_NQ2','STOCK_CLASS_NQ2','YOY_OVER_30PCT_NQ2','QOQ_OVER_10PCT_NQ2',
                   'PER_NQ3','PBR_NQ3','PSR_NQ3','KR_3YBOND_INT_RATE_NQ3','PLUS_NET_INCO_NQ3','NCAV_NQ3','KODEX200MA60_NQ3','STOCK_CLASS_NQ3','YOY_OVER_30PCT_NQ3','QOQ_OVER_10PCT_NQ3',
                   'PER_NQ4','PBR_NQ4','PSR_NQ4','KR_3YBOND_INT_RATE_NQ4','PLUS_NET_INCO_NQ4','NCAV_NQ4','KODEX200MA60_NQ4','STOCK_CLASS_NQ4','YOY_OVER_30PCT_NQ4','QOQ_OVER_10PCT_NQ4',
                   'PER_NQ5','PBR_NQ5','PSR_NQ5','KR_3YBOND_INT_RATE_NQ5','PLUS_NET_INCO_NQ5','NCAV_NQ5','KODEX200MA60_NQ5','STOCK_CLASS_NQ5','YOY_OVER_30PCT_NQ5','QOQ_OVER_10PCT_NQ5',
                   'PER_NQ6','PBR_NQ6','PSR_NQ6','KR_3YBOND_INT_RATE_NQ6','PLUS_NET_INCO_NQ6','NCAV_NQ6','KODEX200MA60_NQ6','STOCK_CLASS_NQ6','YOY_OVER_30PCT_NQ6','QOQ_OVER_10PCT_NQ6',
                   'PER_NQ7','PBR_NQ7','PSR_NQ7','KR_3YBOND_INT_RATE_NQ7','PLUS_NET_INCO_NQ7','NCAV_NQ7','KODEX200MA60_NQ7','STOCK_CLASS_NQ7','YOY_OVER_30PCT_NQ7','QOQ_OVER_10PCT_NQ7',
                   'NEXT_Q_OVER_10PCT_NQ8'
                   ]
dataset.index.name = 'fiscal_company'
# mark all NA values with 0
dataset.fillna(0, inplace=True)

dataset.drop(labels=['PLUS_NET_INCO','PLUS_NET_INCO_NQ1','PLUS_NET_INCO_NQ2','PLUS_NET_INCO_NQ3','PLUS_NET_INCO_NQ4','PLUS_NET_INCO_NQ5','PLUS_NET_INCO_NQ6','PLUS_NET_INCO_NQ7'], axis=1, inplace=True)
dataset.drop(labels=['YOY_OVER_30PCT','YOY_OVER_30PCT_NQ1','YOY_OVER_30PCT_NQ2','YOY_OVER_30PCT_NQ3','YOY_OVER_30PCT_NQ4','YOY_OVER_30PCT_NQ5','YOY_OVER_30PCT_NQ6','YOY_OVER_30PCT_NQ7'], axis=1, inplace=True)
dataset.drop(labels=['QOQ_OVER_10PCT','QOQ_OVER_10PCT_NQ1','QOQ_OVER_10PCT_NQ2','QOQ_OVER_10PCT_NQ3','QOQ_OVER_10PCT_NQ4','QOQ_OVER_10PCT_NQ5','QOQ_OVER_10PCT_NQ6','QOQ_OVER_10PCT_NQ7'], axis=1, inplace=True)
dataset.drop(labels=['NCAV','NCAV_NQ1','NCAV_NQ2','NCAV_NQ3','NCAV_NQ4','NCAV_NQ5','NCAV_NQ6','NCAV_NQ7'], axis=1, inplace=True)
dataset.drop(labels=['KODEX200MA60','KODEX200MA60_NQ1','KODEX200MA60_NQ2','KODEX200MA60_NQ3','KODEX200MA60_NQ4','KODEX200MA60_NQ5','KODEX200MA60_NQ6','KODEX200MA60_NQ7'], axis=1, inplace=True)
dataset.drop(labels=['STOCK_CLASS','STOCK_CLASS_NQ1','STOCK_CLASS_NQ2','STOCK_CLASS_NQ3','STOCK_CLASS_NQ4','STOCK_CLASS_NQ5','STOCK_CLASS_NQ6','STOCK_CLASS_NQ7'], axis=1, inplace=True)

values = dataset.values

# ensure all data is float
values = values.astype('float32')
#print(values[0:5])
# normalize features
# scaler = MinMaxScaler(feature_range=(0,1))
# values = scaler.fit_transform(values)
print(dataset.columns)
print(values[0:5])

# split into train and test sets by n%
print('values.shape =',values.shape)
n_pct = 0.8
pct_cut_point = (int)(values.shape[0] * n_pct)
# random shuffle and split
np.random.shuffle(values)
train = values[:pct_cut_point, :]
print('train.shape =', train.shape)
test = values[pct_cut_point+1:, :]
print('test.shape =', test.shape)


#hyperparameters
n_timesteps = 8
n_features = 10 - 6
learning_rate = 0.001
batch_size = 32
epochs=10

# split into input and output
input_dim = n_timesteps * n_features
print('input_dim=',input_dim)
train_X, train_y = train[:, :input_dim], train[:, -1]
print('train_X.shape = ', train_X.shape)
print('train_y.shape = ', train_y.shape)
print('len(train_X)', len(train_X))
test_X, test_y = test[:, :input_dim], test[:, -1]
print('test_X.shape = ', test_X.shape)
print('test_y.shape = ', test_y.shape)
print(train_X.shape, train_y.shape, test_X.shape, test_y.shape)

# design network
model = Sequential()
model.add(Dense(150, input_dim=input_dim))
model.add(BatchNormalization())
model.add(Activation('relu'))

model.add(Dense(200,  activation='relu'))
model.add(BatchNormalization())
model.add(Activation('relu'))

model.add(Dense(200,  activation='relu'))
model.add(BatchNormalization())
model.add(Activation('relu'))

model.add(Dense(100,  activation='relu'))
model.add(BatchNormalization())
model.add(Activation('relu'))

model.add(Dense(50,  activation='relu'))
model.add(BatchNormalization())
model.add(Activation('relu'))

model.add(Dense(25,  activation='relu'))
model.add(BatchNormalization())
model.add(Activation('relu'))

model.add(Dense(10,  activation='relu'))
model.add(BatchNormalization())
model.add(Activation('relu'))

model.add(Dense(1, activation='sigmoid'))
adam = optimizers.adam(lr=learning_rate)
model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
model.summary()
#fit network
history = model.fit(train_X, train_y, epochs=epochs, batch_size=batch_size, validation_data=(test_X, test_y))
# plot history
plt.plot(history.history['loss'], label='train')
plt.plot(history.history['val_loss'], label='test')
plt.legend()
plt.show()

#evaluation
scores = model.evaluate(test_X, test_y, verbose=1)
print("Accuracy: %.2f%%" % (scores[1]*100))

# prediction
yhat_test = model.predict(test_X, batch_size=1)
print(yhat_test)
i = 0
for yhat in yhat_test:
    if(yhat > 0.5) :
        i = i + 1

print('>0.5', i)
