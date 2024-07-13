package com.example.myapplication.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 1:
                return AIFragment.newInstance("药物咨询");
            case 2:
                return MedicineFragment.newInstance("中药仓库");

            default:
                return MainFragment.newInstance("中药识别");
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}