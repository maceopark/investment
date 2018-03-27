import pandas as pd
import os
import matplotlib.pyplot as plt
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error
from keras.models import Sequential
from keras.layers import Dense, LSTM, Bidirectional, TimeDistributed
from math import sqrt
from numpy import concatenate
from keras import optimizers

FILE_NAME = 'kospi200_price.csv'
DATA_PATH = os.path.join('c:\\', 'git-repo','investment','model','data')
FILE_FULL_PATH = os.path.join(DATA_PATH, FILE_NAME)

# yyyymmdd column is index column
dataset = pd.read_csv(FILE_FULL_PATH, header=0, index_col=1)
# drop stock_name column
dataset.drop('stock_name', axis=1, inplace=True)
# manually specify column names
dataset.columns = ['final_price','start_price','high_price','low_price','trade_volume','int_kr_tr_bond_3y']
dataset.index.name = 'date'
# mark all NA values with 0
dataset.fillna(0, inplace=True)

#print(dataset.head(5))

def plot_time_series(dataset):
    values = dataset.values
    groups = [0, 1, 2, 3, 4, 5]
    i = 1
    plt.figure()
    for group in groups:
        plt.subplot(len(groups), 1, i)
        plt.plot(values[:, group])
        plt.title(dataset.columns[group])
        i += 1

    plt.show()

def series_to_supervised(data, n_in=1, n_out=1, dropnan=True):
    n_vars = 1 if type(data) is list else data.shape[1]
    df = pd.DataFrame(data)
    cols, names = [], []
    # input sequnce (t-n, ... , t-1)
    for i in range(n_in, 0, -1):
        cols.append(df.shift(i))
        names += [('var%d(t-%d)' % (j+1, i)) for j in range(n_vars)]

    # forecast sequence (t, t+1, ... t+n)
    for i in range(0, n_out):
        cols.append(df.shift(-i))
        if i == 0:
            names += [('var%d(t)' % (j+1)) for j in range(n_vars)]
        else:
            names += [('var%d(t+%d)' % (j+1, i)) for j in range(n_vars)]

    # put it all together
    agg = pd.concat(cols, axis=1)
    agg.columns = names
    # drop rows with NaN values
    if dropnan:
        agg.dropna(inplace=True)
    return agg

values = dataset.values


n_timesteps = 20
n_features = 6

# ensure all data is float
values = values.astype('float32')
# normalize features
scaler = MinMaxScaler(feature_range=(0,1))
scaled = scaler.fit_transform(values)
#print(dataset.columns)
#print(scaled[0:5])
# frame as supervised learning
#reframed = series_to_supervised(scaled, n_in=n_timesteps, n_out=1)
reframed = series_to_supervised(values, n_in=n_timesteps, n_out=1)
#print('before column drop')
#print(reframed.head())
# drop columns we don't want to predict
reframed.drop(reframed.columns[[n_features*n_timesteps+i+1 for i in range(n_features-1)]], axis=1, inplace=True)
reframed['var1(t)'] = (reframed['var1(t-1)'] < reframed['var1(t)']).astype(int)
#print('after column drop')
print(reframed.head())


# split into train and test sets
values = reframed.values
print('values.shape =',values.shape)
train = values[:3429, :]        # 3804 samples * 0.9
print('train.shape =', train.shape)
test = values[3430:, :]
print('test.shape =', test.shape)
# split into input and output
n_obs = n_timesteps * n_features # 20x6
train_X, train_y = train[:, :n_obs], train[:, -1]
print('train_X.shape = ', train_X.shape)
print('train_y.shape = ', train_y.shape)
print('len(train_X)', len(train_X))
test_X, test_y = test[:, :n_obs], test[:, -1]
print('test_X.shape = ', test_X.shape)
print('test_y.shape = ', test_y.shape)
# reshape input to be 3D [samples, timesteps, features]
train_X = train_X.reshape((train_X.shape[0], n_timesteps, n_features))
test_X = test_X.reshape((test_X.shape[0], n_timesteps, n_features))
print(train_X.shape, train_y.shape, test_X.shape, test_y.shape)

learning_rate = 0.0001
# design network
model = Sequential()
model.add(LSTM(n_timesteps, return_sequences=True, input_shape=(train_X.shape[1], train_X.shape[2])))
model.add(LSTM(n_timesteps))
model.add(Dense(1, activation='sigmoid'))
adam = optimizers.adam(lr=learning_rate)
model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
model.summary()
#fit network
history = model.fit(train_X, train_y, epochs=10, batch_size=64, validation_data=(test_X, test_y))
# plot history
plt.plot(history.history['loss'], label='train')
plt.plot(history.history['val_loss'], label='test')
plt.legend()
plt.show()

scores = model.evaluate(test_X, test_y, verbose=1)
print("Accuracy: %.2f%%" % (scores[1]*100))

# make a prediction
#yhat = model.predict(test_X)
#test_X = test_X.reshape((test_X.shape[0], n_timesteps*n_features))
# invert scaling for forecast
#inv_yhat = concatenate((yhat, test_X[:, -(n_features-1):]), axis=1)
#inv_yhat = scaler.inverse_transform(inv_yhat)
#inv_yhat = inv_yhat[:, 0]
# invert scaling for actual
#test_y = test_y.reshape((len(test_y), 1))
#inv_y = concatenate((test_y, test_X[:, -(n_features-1):]), axis=1)
#inv_y = scaler.inverse_transform(inv_y)
#inv_y = inv_y[:, 0]
# calc RMSE
#rmse = sqrt(mean_squared_error(inv_y, inv_yhat))
#print('Test RMSE: %.3f' % rmse)
#print('inv_y: ', inv_y.shape)
#print('inv_yhat: ', inv_yhat.shape)

#plt.plot(inv_y, label='actual')
#plt.plot(inv_yhat, label='predicted')
#plt.legend()
#plt.show()
