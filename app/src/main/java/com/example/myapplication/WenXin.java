package com.example.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WenXin {

    private static final String TAG = "WenXin";
    public static final String API_KEY = "OU8Y61XHuSF730mcjVLiVWce";
    public static final String SECRET_KEY = "7CLAgULju0y3pUrJrUWG2Ie64Uh2Sg4T";
    public JSONArray DialogContent;

    public WenXin() {
        DialogContent = new JSONArray();
    }

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    // 用于获取回答
    public String getAnswer(String question) throws IOException, JSONException {
        // 创建JSONObject
        JSONObject jsonObject = new JSONObject();
        // 添加键值对
        jsonObject.put("role", "user");
        jsonObject.put("content", question);

        // 将JSONObject添加到JSONArray中
        DialogContent.put(jsonObject);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"messages\":" +
                DialogContent.toString() +
                ",\"disable_search\":false,\"enable_citation\":false}");

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-speed-128k?access_token=" +
                        getAccessToken())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // 重试机制
        int tryCount = 0;
        IOException exception = null;
        while (tryCount < 3) {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                //解析出文心一言的回答
                JSONObject json_feedback = new JSONObject(response.body().string());
                String re = json_feedback.getString("result");
                //把文心一言的回答加入到Dialogue_Content中
                JSONObject jsontmp = new JSONObject();
                jsontmp.put("assistant", re);
                DialogContent.put(jsontmp);

                return re;
            } catch (IOException e) {
                exception = e;
                tryCount++;
                Log.w(TAG, "Request failed - attempt " + tryCount, e);
            }
        }

        throw exception;
    }

    public String getAccessToken() throws IOException, JSONException {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return new JSONObject(response.body().string()).getString("access_token");
        }
    }
}
