package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.myapplication.entity.HerbItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class HerbParser {
    private Context context;

    public HerbParser(Context context) {
        this.context = context;
    }

    public List<HerbItem> createHerbs() {
        List<HerbItem> herbList = null;
        AssetManager assetManager = context.getAssets();
        Gson gson = new Gson();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("herbs.json")))) {
            Type listType = new TypeToken<List<HerbItem>>() {}.getType();
            herbList = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return herbList;
    }
}
