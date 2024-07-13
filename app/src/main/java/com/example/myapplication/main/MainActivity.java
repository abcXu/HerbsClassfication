package com.example.myapplication.main;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tv_title = findViewById(R.id.text_top_main);
        initViews();
        setupViewPager();
        setupBottomNavigationView();

    }

    private void initViews() {
        tv_title.setText("智能识别");
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_recognition);
                        updateTitle(0);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
                        updateTitle(1);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_medicines);
                        updateTitle(2);
                        break;
                }
            }
        });
    }
    private void updateTitle(int position) {
        switch (position) {
            case 0:
                tv_title.setText("智能识别");
                break;
            case 1:
                tv_title.setText("药物咨询");
                break;
            case 2:
                tv_title.setText("中药仓库");
                break;
        }
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_recognition:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.nav_chat:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.nav_medicines:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
    }

}
