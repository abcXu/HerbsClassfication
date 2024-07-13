import os
import sys
import json
import pickle
import random

import torch
from tqdm import tqdm

import matplotlib.pyplot as plt




def read_split_data(data_path):
    """

    :param data_path: 数据路径
    :return: 训练集、验证集的图片路径以及对应的标签
    """
    # 确保数据的根目录存在
    assert os.path.exists(data_path), "dataset root:{} does not exist.".format(data_path)

    train_images_path = []
    train_images_label = []
    val_images_path = []
    val_images_label = []
    # 训练数据集的路径存储在data_path路径下train.txt文件中
    # 读取训练数据的路径及对应的标签
    with open(os.path.join(data_path, "train.txt"), "r") as f:
        for line in f:
            # 读取到的数据格式为: 训练图片路径 标签
            # 删除换行符
            line = line.strip()
            # 获取图片路径
            images_path = os.path.join(data_path, line.split(" ")[0])
            # 获取图片标签
            images_label = int(line.split(" ")[1])
            # 添加数据
            train_images_path.append(images_path)
            train_images_label.append(images_label)
    with open(os.path.join(data_path, "val.txt"), "r") as f:
        for line in f:
            line = line.strip()
            images_path = os.path.join(data_path, line.split(" ")[0])
            images_label = int(line.split(" ")[1])
            val_images_path.append(images_path)
            val_images_label.append(images_label)


    return train_images_path, train_images_label, val_images_path, val_images_label

if __name__ == '__main__':

    root_path = "D:/Datasets/cls_chinese_medicine"
    train_images_path, train_images_label, val_images_path, val_images_label = read_split_data(root_path)
    print("done!")
    path = 'D:/Datasets/cls_chinese_medicine\\train/aiye/aiye_0001.jpg'
    plt.imshow(plt.imread(path))
    plt.show()