from PIL import Image
import torch
from torch.utils.data import Dataset



class MyDataSet(Dataset):
    """自定义数据集"""

    def __init__(self, images_path: list, images_class: list, transform=None):
        self.images_path = images_path  # 图片路径
        self.images_class = images_class  # 图片类别
        self.transform = transform    # 转换

    def __len__(self):
        return len(self.images_path)    # 返回数据集的长度

    def __getitem__(self, item):
        img = Image.open(self.images_path[item])    # 获取图片
        # RGB为彩色图片，L为灰度图片
        if img.mode != 'RGB':   # 如果图片不是RGB图片则抛出异常
            raise ValueError("image: {} isn't RGB mode.".format(self.images_path[item]))
        label = self.images_class[item]     # 获取矩阵

        if self.transform is not None:  # 如果需要对图片进行transform
            img = self.transform(img)

        return img, label   # 返回图片和标签

    @staticmethod
    def collate_fn(batch):  # batch是一个包含多个数据样本的列表参数
        # 官方实现的default_collate可以参考
        # https://github.com/pytorch/pytorch/blob/67b7e751e6b5931a9f45274653f4f653a4e6cdf6/torch/utils/data/_utils/collate.py

        images, labels = tuple(zip(*batch))     # 将图像和标签分别提取出来，得到两个元组
        images = torch.stack(images, dim=0)
        labels = torch.as_tensor(labels)
        return images, labels


if __name__ == "__main__":
    image_path = "D:/Datasets/cls_chinese_medicine/"