package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.myapplication.entity.HerbItem;

public class HerbDetailActivity extends AppCompatActivity {

    private ImageView herbImage;
    private TextView herbName;
    private TextView herbDescription;
    private TextView herbSource;
    private TextView herbEffect;
    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herb_detail);

        herbImage = findViewById(R.id.herb_image);
        herbName = findViewById(R.id.herb_name);
        herbDescription = findViewById(R.id.herb_description);
        herbSource = findViewById(R.id.herb_source);
        herbEffect = findViewById(R.id.herb_effect);
        btn_back = findViewById(R.id.btn_back);
        Intent intent = getIntent();
        if (intent != null){
            receiveData(intent);
        }
        btn_back.setOnClickListener(v -> finish());

    }

    public void receiveData(Intent intent)
    {
        String name = intent.getStringExtra("name");
        herbName.setText(name);
        String image = intent.getStringExtra("image");
        // 使用Glide加载，以及占位图
        Glide.with(this).load(image).placeholder(R.drawable.error_placeholder).into(herbImage);
        herbDescription.setText(intent.getStringExtra("description"));
        herbSource.setText(intent.getStringExtra("source"));
        herbEffect.setText(intent.getStringExtra("effect"));


    }
}