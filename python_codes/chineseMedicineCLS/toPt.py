import torch
import argparse
from timm import create_model


def save_model(model, path='model.pt'):
    # 将模型设置为评估模式
    model.eval()

    # 创建一个具有正确尺寸的示例输入张量
    example_input = torch.randn(1, 3, 224, 224)  # 根据你的模型输入尺寸调整

    # 使用脚本化模型
    with torch.jit.optimized_execution(True):
        scripted_model = torch.jit.script(model)

    # 保存脚本化的模型到指定路径
    scripted_model.save(path)


if __name__ == "__main__":
    # 创建模型
    model = create_model('swin_base_patch4_window7_224', pretrained=False)

    # 用新的25类分类器替换模型头部
    model.head = torch.nn.Linear(model.head.in_features, out_features=25)

    # 将训练好的权重加载到模型中
    model.load_state_dict(torch.load('./weights/best.pth'))

    # 指定保存脚本化模型的路径
    save_path = './results/model.pt'

    # 调用 save_model 函数来保存脚本化的模型
    save_model(model, save_path)
