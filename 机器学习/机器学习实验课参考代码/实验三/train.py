import cv2
import os
from PIL import Image
from tqdm import tqdm
from feature import *
from ensemble import *
from sklearn.tree import DecisionTreeClassifier
from sklearn import datasets as ds
from sklearn import model_selection as ms
from sklearn.metrics import classification_report
from numpy import *
from shutil import copy
import matplotlib.pyplot as plt
import pickle

face_path = "datasets\\original\\face"
nonface_path = "datasets\\original\\nonface"
face_grayscale_path = "datasets\\original\\face_grayscale"
nonface_grayscale_path = "datasets\\original\\nonface_grayscale"
face_feature_path = "datasets\\original\\face_feature"
nonface_feature_path = "datasets\\original\\nonface_feature"
train_path = "datasets\\original\\train"
val_path = "datasets\\original\\val"
save_model_path = "save_model"


def getDir(filepath):
    pathlist=os.listdir(filepath)
    return pathlist

def save_feature(feature, filename):
    with open(filename, "wb") as f:
        pickle.dump(feature, f)

def load_feature(filename):
    with open(filename, "rb") as f:
        return pickle.load(f)

def imgToArray(img):
    im=numpy.array(img)        #转化为二维数组
    for i in range(im.shape[0]):#转化为二值矩阵
        for j in range(im.shape[1]):
            if im[i,j]==75 or im[i,j]==76:
                im[i,j]=1
            else:
                im[i,j]=0
    return im

def imgToGrayscale():
    '''
    读取数据集的图片并转换为24*24的灰度图片，保存在face_grayscale和nonface_grayscale
    :return: None
    '''

    face_fileList = getDir(face_path)
    for img in tqdm(face_fileList):
        I = Image.open(f'{face_path}\\{img}')
        # I.show()
        L = I.convert('L')
        L = L.resize((24, 24), Image.ANTIALIAS)
        # L.show()
        L.save(f'{face_grayscale_path}\\{img}')

    nonface_fileList = getDir(nonface_path)
    for img in tqdm(nonface_fileList):
        I = Image.open(f'{nonface_path}\\{img}')
        # I.show()
        L = I.convert('L')
        L = L.resize((24, 24), Image.ANTIALIAS)
        # L.show()
        L.save(f'{nonface_grayscale_path}\\{img}')

def feature_extract():
    face_grayscale_fileList = getDir(face_grayscale_path)
    nonface_grayscale_fileList = getDir(nonface_grayscale_path)

    for img in tqdm(face_grayscale_fileList):
        I = Image.open(f'{face_grayscale_path}\\{img}')
        im = imgToArray(I)
        npd = NPDFeature(im)
        npd_feature = npd.extract()
        img = img.replace('.jpg','')
        AdaBoostClassifier.save(npd_feature,f'{face_feature_path}\\{img}.pickle')

    for img in tqdm(nonface_grayscale_fileList):
        I = Image.open(f'{nonface_grayscale_path}\\{img}')
        im = imgToArray(I)
        npd = NPDFeature(im)
        npd_feature = npd.extract()
        img = img.replace('.jpg','')
        AdaBoostClassifier.save(npd_feature, f'{nonface_feature_path}\\{img}.pickle')

def dataset_split_jpg(train_val_split = 0.2): # 验证集占比 ，默认为0.2
      face_grayscale_fileList = getDir(face_grayscale_path)
      nonface_grayscale_fileList = getDir(nonface_grayscale_path)
      lenOf_face_grayscale_fileList = len(face_grayscale_fileList)
      lenOf_nonface_grayscale_fileList = len(nonface_grayscale_fileList)

      index = 0
      for img in tqdm(face_grayscale_fileList):
          I = Image.open(f'{face_grayscale_path}\\{img}')
          if index < train_val_split * lenOf_face_grayscale_fileList:
              I.save(f'{val_path}\\{img}')
          else:
              I.save(f'{train_path}\\{img}')
          index += 1


      index = 0
      for img in tqdm(nonface_grayscale_fileList):
          I = Image.open(f'{nonface_grayscale_path}\\{img}')
          if index < train_val_split * lenOf_face_grayscale_fileList:
              I.save(f'{val_path}\\{img}')
          else:
              I.save(f'{train_path}\\{img}')
          index += 1

def dataset_split_feature(train_val_split = 0.2): # 验证集占比 ，默认为0.2
    face_feature_filelist = getDir(face_feature_path)
    nonface_feature_filelist = getDir(nonface_feature_path)
    lenOf_face_feature_fileList = len(face_feature_filelist)
    lenOf_nonface_feature_fileList = len(nonface_feature_filelist)

    index = 0
    for fea in tqdm(face_feature_filelist):
        if index < train_val_split * lenOf_face_feature_fileList:
            copy(face_feature_path + '\\' + fea, val_path + '\\' + fea)
        else:
            copy(face_feature_path + '\\' + fea, train_path + '\\' + fea)
        index += 1

    index = 0
    for fea in tqdm(nonface_feature_filelist):
        if index < train_val_split * lenOf_nonface_feature_fileList:
            copy(nonface_feature_path + '\\' + fea, val_path + '\\' + fea)
        else:
            copy(nonface_feature_path + '\\' + fea, train_path + '\\' + fea)
        index += 1



def load_data(path): #加载训练数据
    fileList = getDir(path)
    X=[]
    y=[]
    for file in tqdm(fileList):
        fea = load_feature(f'{path}\\{file}')
        X.append(list(fea))
        if file.find('non'):
            y.append(-1)
        else:
            y.append(1)
    return X,y



if __name__ == "__main__":
    #X_train, y_train = load_data(train_path)
    #X_train = array(X_train)
    #y_train = array(y_train).reshape(-1,1)

    #Ada = AdaBoostClassifier(DecisionTreeClassifier(),5)
    #Ada.fit(X_train,y_train)

    #AdaBoostClassifier.save(Ada,save_model_path + '\\model.pickle')
    Ada = AdaBoostClassifier.load(save_model_path + '\\model.pickle')

    X_val, y_val = load_data(val_path)
    X_val = array(X_val)
    y_val = array(y_val).reshape(-1,1)

    y_pre = Ada.predict(X_val)
    # 打印正类负类
    report = classification_report(y_val, where(y_val == y_pre, 1, -1), target_names=["positive", "negative"],digits=4)
    print(report)

    f1 = open('classifier_report.txt', 'w')
    f1.write(report)
    f1.close()








