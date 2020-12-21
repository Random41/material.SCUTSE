import pickle
from sklearn.tree import DecisionTreeClassifier
from numpy import *
from tqdm import tqdm
from sklearn.metrics import classification_report

class AdaBoostClassifier:
    '''A simple AdaBoost Classifier.'''

    def __init__(self, weak_classifier, n_weakers_limit):
        '''Initialize AdaBoostClassifier

        Args:
            weak_classifier: The class of weak classifier, which is recommend to be sklearn.tree.DecisionTreeClassifier.
            n_weakers_limit: The maximum number of weak classifier the model can use.
        '''
        self.base_classifier = weak_classifier
        self.n_weakers_limit = n_weakers_limit


    def is_good_enough(self):
        '''Optional'''
        return self.errorRate == 0

    def fit(self,X,y):
        '''Build a boosted classifier from the training set (X, y).

        Args:
            X: An ndarray indicating the samples to be trained, which shape should be (n_samples,n_features).
            y: An ndarray indicating the ground-truth labels correspond to X, which shape should be (n_samples,1).
        '''
        m, n = X.shape #样本的数量和特征数量
        self.W = ones((m,1)) / m #样本的权值
        self.base_classifier_list = [] #基分类器列表
        aggClassEst = zeros(m)
        inf = 1e9
        self.errorRate = inf

        for i in tqdm(range(self.n_weakers_limit)):
            base_classifier = self.base_classifier
            base_classifier.fit(X,y) #训练基分类器
            predictedVals = base_classifier.predict(X).reshape(-1,1) #预测值
            errArr = ones((m,1))
            errArr[predictedVals == y] = 0  # 预测值与实际值相同，误差置为0
            error = dot(self.W.T , errArr)  # D就是每个样本点的权值，随着迭代，它会变化，这段代码是误差率的公式
            alpha = float(0.5 * log((1.0 - error) / max(error, 1e-16))) #计算alpha

            base_classifier_tmp = list()
            base_classifier_tmp.append(base_classifier)
            base_classifier_tmp.append(alpha)
            self.base_classifier_list.append(base_classifier_tmp) #将基分类器加入到adaboost的列表中

            expon = -1 * alpha * y * predictedVals
            self.W = self.W * exp(expon)  # 根据公式 w^m+1 = w^m (e^-a*y^i*G)/Z^m 更新W
            self.W = self.W / self.W.sum()  #归一化

            aggErrors = (sign(aggClassEst) != y) * ones((m, 1))  # 分错的矩阵
            self.errorRate = aggErrors.sum() / m  # 分错的个数除以总数，就是分类误差率
            if self.is_good_enough():  # 误差率满足要求，则break退出
                break


    def predict_scores(self, X):
        '''Calculate the weighted sum score of the whole base classifiers for given samples.

        Args:
            X: An ndarray indicating the samples to be predicted, which shape should be (n_samples,n_features).

        Returns:
            An one-dimension ndarray indicating the scores of differnt samples, which shape should be (n_samples,1).
        '''

        m = X.shape[0]
        predictedVals = zeros((m,1))
        for classifier in self.base_classifier_list:
            base_classifier = classifier[0]
            alpha = classifier[1]
            pre = base_classifier.predict(X).reshape(-1,1)
            predictedVals += alpha * pre
        return predictedVals

    def predict(self, X, threshold=0):
        '''Predict the catagories for geven samples.

        Args:
            X: An ndarray indicating the samples to be predicted, which shape should be (n_samples,n_features).
            threshold: The demarcation number of deviding the samples into two parts.

        Returns:
            An ndarray consists of predicted labels, which shape should be (n_samples,1).
        '''

        predictedVals = self.predict_scores(X)
        result = zeros((predictedVals.shape[0],1))
        result[predictedVals > threshold] = 1
        result[predictedVals <= threshold] = -1
        return result



    @staticmethod
    def save(model, filename):
        with open(filename, "wb") as f:
            pickle.dump(model, f)

    @staticmethod
    def load(filename):
        with open(filename, "rb") as f:
            return pickle.load(f)
