import torch
from torchvision.transforms import transforms
from medicineDataset import MyDataSet
from torch.utils.data import DataLoader
from utils import read_split_data
import argparse
from timm import create_model
from functions import train_one_epoch,evaluate
import torch.nn as nn
import matplotlib.pyplot as plt
def main(args):
    data_transform = {
        "train": transforms.Compose([transforms.RandomResizedCrop(224),  # 裁剪出一块大小为224的图片区域
                                     transforms.RandomHorizontalFlip(),
                                     transforms.ToTensor(),
                                     transforms.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])]),
        "val": transforms.Compose([transforms.Resize(256),  # H，W均调整为256
                                   transforms.CenterCrop(224),  # 从中心裁剪
                                   transforms.ToTensor(),  # 转换为tensor
                                   transforms.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])])}

    train_images_path, train_labels, val_images_path, val_labels = read_split_data(args.data_path)

    # 加载训练数据集和测试数据集
    train_dataset = MyDataSet(images_path=train_images_path,
                              images_class=train_labels,
                              transform=data_transform["train"])
    val_dataset = MyDataSet(images_path=val_images_path,
                            images_class=val_labels,
                            transform=data_transform["val"])
    batch_size = args.batch_size

    # 创建数据加载器
    train_loader = DataLoader(train_dataset,
                              batch_size=batch_size,
                              shuffle=True,
                              pin_memory=True,
                              num_workers=4)
    val_loader = DataLoader(val_dataset,
                            batch_size=batch_size,
                            shuffle=False,
                            pin_memory=True,
                            num_workers=4)

    print("{} images were loaded for train".format(len(train_dataset)))
    print("{} images were loaded for validation".format(len(val_dataset)))

    model = create_model(args.model_name,pretrained=True)
    model.head = nn.Linear(model.head.in_features, args.num_classes)  # 替换掉模型的分类层
    model.to(args.device)

    # 冻结
    if args.freeze_layers:
        for name,para in model.named_parameters():
            if "head" not in name:  # 如果不在head层，则冻结
                para.requires_grad = False
            else:
                print("training {}".format(name))

    optimizer = torch.optim.Adam(filter(lambda p: p.requires_grad, model.parameters()), lr=args.lr)
    bst = 0
    train_losses, val_losses = [],[]
    for epoch in range(args.epochs):
        # train
        train_loss, train_acc = train_one_epoch(model=model,
                                                optimizer=optimizer,
                                                data_loader=train_loader,
                                                device=args.device,
                                                epoch=epoch)
        # validate
        val_loss, val_acc = evaluate(model=model,
                                     data_loader=val_loader,
                                     device=args.device,
                                     epoch=epoch)
        if val_acc > bst:
            bst = val_acc
            torch.save(model.state_dict(), "./weights/best.pth")
        print("epoch:{} train_loss:{} train_acc:{} val_loss:{} val_acc:{}".format(epoch, train_loss, train_acc, val_loss, val_acc))
        print("best:{}".format(bst))
        train_losses.append(train_loss)
        val_losses.append(val_loss)
        # 保存最后一轮训练的模型
        if epoch == args.epochs - 1:
            torch.save(model.state_dict(), "./weights/model_last.pth")

    # 绘制损失函数
    plt.plot(train_losses, label='train_loss')
    plt.plot(val_losses, label='val_loss')
    plt.legend()
    plt.xlabel('epoch')
    plt.ylabel('loss')
    plt.title('loss')
    plt.show()
    plt.savefig("./weights/loss.png")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--num_classes', type=int, default=25,help='num of classes')
    parser.add_argument('--epochs', type=int, default=10,help='number of epochs to train (default: 10)')
    parser.add_argument('--batch-size', type=int, default=8,help='input batch size for training (default: 32)')
    parser.add_argument('--lr', type=float, default=0.001,help='learning rate (default: 0.001)')
    parser.add_argument('--lrf', type=float, default=0.01)

    parser.add_argument('--data_path', type=str,default="D:/Datasets/cls_chinese_medicine")
    parser.add_argument('--model_name', default='swin_base_patch4_window7_224', help='create model name')

    # 预训练权重路径，如果不想载入就设置为空字符
    parser.add_argument('--weights', type=str, default='',help='initial weights path')
    # 是否冻结权重
    parser.add_argument('--freeze-layers', type=bool, default=True)
    parser.add_argument('--device', default='cuda:0', help='device id (i.e. 0 or 0,1 or cpu)')

    opt = parser.parse_args()

    main(opt)