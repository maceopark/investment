import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import StandardScaler
from sklearn.cross_validation import train_test_split
from sklearn.metrics import accuracy_score
import os

# visulaize the important characteristics of the dataset
import matplotlib.pyplot as plt

# step 1: download the data
# dataframe_all = pd.read_csv("https://d396qusza40orc.cloudfront.net/predmachlearn/pml-training.csv")


FILE_NAME = 'goldenfishery.csv'
DATA_PATH = os.path.join('c:\\', 'git-repo','investment','model','data')
FILE_FULL_PATH = os.path.join(DATA_PATH, FILE_NAME)

# fiscall_company column is index column
dataframe_all = pd.read_csv(FILE_FULL_PATH, header=0, index_col=0)
# manually specify column names
dataframe_all.columns = ['PER','PBR','PSR','KR_3YBOND_INT_RATE','PLUS_NET_INCO','NCAV','KODEX200MA60','STOCK_CLASS','YOY_OVER_30PCT','QOQ_OVER_10PCT',
                   'PER_NQ1','PBR_NQ1','PSR_NQ1','KR_3YBOND_INT_RATE_NQ1','PLUS_NET_INCO_NQ1','NCAV_NQ1','KODEX200MA60_NQ1','STOCK_CLASS_NQ1','YOY_OVER_30PCT_NQ1','QOQ_OVER_10PCT_NQ1',
                   'PER_NQ2','PBR_NQ2','PSR_NQ2','KR_3YBOND_INT_RATE_NQ2','PLUS_NET_INCO_NQ2','NCAV_NQ2','KODEX200MA60_NQ2','STOCK_CLASS_NQ2','YOY_OVER_30PCT_NQ2','QOQ_OVER_10PCT_NQ2',
                   'PER_NQ3','PBR_NQ3','PSR_NQ3','KR_3YBOND_INT_RATE_NQ3','PLUS_NET_INCO_NQ3','NCAV_NQ3','KODEX200MA60_NQ3','STOCK_CLASS_NQ3','YOY_OVER_30PCT_NQ3','QOQ_OVER_10PCT_NQ3',
                   'PER_NQ4','PBR_NQ4','PSR_NQ4','KR_3YBOND_INT_RATE_NQ4','PLUS_NET_INCO_NQ4','NCAV_NQ4','KODEX200MA60_NQ4','STOCK_CLASS_NQ4','YOY_OVER_30PCT_NQ4','QOQ_OVER_10PCT_NQ4',
                   'PER_NQ5','PBR_NQ5','PSR_NQ5','KR_3YBOND_INT_RATE_NQ5','PLUS_NET_INCO_NQ5','NCAV_NQ5','KODEX200MA60_NQ5','STOCK_CLASS_NQ5','YOY_OVER_30PCT_NQ5','QOQ_OVER_10PCT_NQ5',
                   'PER_NQ6','PBR_NQ6','PSR_NQ6','KR_3YBOND_INT_RATE_NQ6','PLUS_NET_INCO_NQ6','NCAV_NQ6','KODEX200MA60_NQ6','STOCK_CLASS_NQ6','YOY_OVER_30PCT_NQ6','QOQ_OVER_10PCT_NQ6',
                   'PER_NQ7','PBR_NQ7','PSR_NQ7','KR_3YBOND_INT_RATE_NQ7','PLUS_NET_INCO_NQ7','NCAV_NQ7','KODEX200MA60_NQ7','STOCK_CLASS_NQ7','YOY_OVER_30PCT_NQ7','QOQ_OVER_10PCT_NQ7',
                   'NEXT_Q_OVER_10PCT_NQ8'
                   ]
dataframe_all.index.name = 'fiscal_company'
# mark all NA values with 0
dataframe_all.fillna(0, inplace=True)

num_rows = dataframe_all.shape[0]

# step 2: remove useless data
# count the number of missing elements (NaN) in each column
counter_nan = dataframe_all.isnull().sum()
counter_without_nan = counter_nan[counter_nan==0]
# remove the columns with missing elements
dataframe_all = dataframe_all[counter_without_nan.keys()]
# remove the first 7 columns which contain no discriminative information
# dataframe_all = dataframe_all.ix[:,7:]
# the list of columns (the last column is the class label)
columns = dataframe_all.columns
print(columns)

# step 3: get features (x) and scale the features
# get x and convert it to numpy array
x = dataframe_all.ix[:,:-1].values
standard_scaler = StandardScaler()
x_std = standard_scaler.fit_transform(x)

# step 4: get class labels y and then encode it into number
# get class label data
y = dataframe_all.ix[:,-1].values
# encode the class label
class_labels = np.unique(y)
label_encoder = LabelEncoder()
y = label_encoder.fit_transform(y)

# step 5: split the data into training set and test set
test_percentage = 0.10
x_train, x_test, y_train, y_test = train_test_split(x_std, y, test_size = test_percentage, random_state = 0)

# t-distributed Stochastic Neighbor Embedding (t-SNE) visualization
from sklearn.manifold import TSNE
tsne = TSNE(n_components=2, random_state=0)
x_test_2d = tsne.fit_transform(x_test)

# scatter plot the sample points among 5 classes
markers=('s', 'd', 'o', '^', 'v')
color_map = {0:'red', 1:'blue', 2:'lightgreen', 3:'purple', 4:'cyan'}
plt.figure()
for idx, cl in enumerate(np.unique(y_test)):
    plt.scatter(x=x_test_2d[y_test==cl,0], y=x_test_2d[y_test==cl,1], c=color_map[idx], marker=markers[idx], label=cl)
plt.xlabel('X in t-SNE')
plt.ylabel('Y in t-SNE')
plt.legend(loc='upper left')
plt.title('t-SNE visualization of test data')
plt.show()