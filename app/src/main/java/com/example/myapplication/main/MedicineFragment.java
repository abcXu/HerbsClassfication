package com.example.myapplication.main;

//import static com.example.myapplication.HerbDetailActivity.EXTRA_HERB;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.hardware.lights.LightState;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.myapplication.HerbDetailActivity;
import com.example.myapplication.HerbParser;
import com.example.myapplication.R;
import com.example.myapplication.adapters.HerbItemAdapter;
import com.example.myapplication.entity.HerbItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MedicineFragment extends Fragment {



    private static final String FRAGMENT_NAME = "fragmentName";
    View rootView;

    private String mFragmentName;
    private RecyclerView recyclerView;
    private HerbItemAdapter adapter;
    private List<HerbItem> herbItemList;



    public MedicineFragment() {
        // Required empty public constructor
    }


    public static MedicineFragment newInstance(String name) {
        MedicineFragment fragment = new MedicineFragment();
        Bundle args = new Bundle();

        args.putString(FRAGMENT_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentName = getArguments().getString(FRAGMENT_NAME);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_medicine, container, false);
        }
        loadHerbs(getContext());
        // 设置适配器
        adapter = new HerbItemAdapter(herbItemList);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        // 设置布局管理器为两列瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
//        MyOnItemClickListener listener = new MyOnItemClickListener();
//        adapter.setOnItemClickListener(listener);
        return rootView;
    }

    private void loadHerbs(Context context){
        // 新开一个子线程读取数据
        new Thread(() -> {
            HerbParser herbParser = new HerbParser(context);
            herbItemList = herbParser.createHerbs();
            if (herbItemList != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter = new HerbItemAdapter(herbItemList);
                    recyclerView.setAdapter(adapter);
                });
            }
        }).start();
    }

//    private class MyOnItemClickListener implements OnItemClickListener {
//
//        @Override
//        public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//            Log.d("MyOnItemClickListener", "onItemClick: " + position);
//            // 携带条目的属性跳转到详情页
//            HerbItem herbItem = (HerbItem) adapter.getItem(position);
//            if (herbItem != null) {
//                // 从fragment 跳转到详情页，并且携带参数
//                Intent intent = new Intent(getActivity(), HerbDetailActivity.class);
//                String name = herbItem.getName();
//                intent.putExtra("name", name);
//                String image = herbItem.getImage();
//                intent.putExtra("image", image);
//                String description = herbItem.getDescription();
//                intent.putExtra("description", description);
//                String source = herbItem.getSource();
//                intent.putExtra("source", source);
//                String effect = herbItem.getEffect();
//                intent.putExtra("effect", effect);
//                startActivity(intent);
//            }
//        }
//    }
}