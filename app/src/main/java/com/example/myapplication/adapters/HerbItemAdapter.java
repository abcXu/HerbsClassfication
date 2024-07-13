package com.example.myapplication.adapters;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.myapplication.HerbDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.entity.HerbItem;

import java.util.List;
import java.util.Random;

public class HerbItemAdapter extends BaseQuickAdapter<HerbItem, BaseViewHolder> {
    private Random random;

    public HerbItemAdapter(@Nullable List<HerbItem> data) {
        super(R.layout.item_herb, data);
        random = new Random();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HerbItem herbItem) {
        helper.setText(R.id.herb_item_name, herbItem.getName());
        helper.setText(R.id.herb_item_description, herbItem.getDescription());
        ImageView iv_herb_image = helper.getView(R.id.herb_item_image);

        // 获取原始高度
        ViewGroup.LayoutParams layoutParams = iv_herb_image.getLayoutParams();
        int originalHeight = layoutParams.height;

        float scaleFactor = 0.1f; // 调整比例，例如10%

        // 计算新的高度
        int randomOffset = (int) (originalHeight * scaleFactor * (random.nextFloat() * 2 - 1));
        int newHeight = originalHeight + randomOffset;

//        // 设置新的高度
//        ViewGroup.LayoutParams layoutParams = iv_herb_image.getLayoutParams();
        layoutParams.height = newHeight;
        iv_herb_image.setLayoutParams(layoutParams);

        Glide.with(iv_herb_image.getContext())
                .load(herbItem.getImage())
                .error(R.drawable.error_placeholder) // 错误占位符图片
                .into(iv_herb_image);

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HerbDetailActivity.class);
                String name = herbItem.getName();
                intent.putExtra("name", name);
                String image = herbItem.getImage();
                intent.putExtra("image", image);
                String description = herbItem.getDescription();
                intent.putExtra("description", description);
                String source = herbItem.getSource();
                intent.putExtra("source", source);
                String effect = herbItem.getEffect();
                intent.putExtra("effect", effect);
                v.getContext().startActivity(intent);
            }
        });

    }
}
