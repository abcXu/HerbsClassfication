package com.example.myapplication.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import org.json.JSONException;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.example.myapplication.WenXin;

import com.example.myapplication.Medicine;

public class MainFragment extends Fragment {

    private static final String FRAGMENT_NAME = "fragmentName";
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int MSG_DETAIL = 3;

    private String mFragmentName;
    private View rootView;
    private ImageView selectedImage,onLoading;
    private TextView classificationResult,timeUsage;
    private Module model;   // pytorch模型

    private String[] classes = Medicine.MEDICINE_NAMES;
    private long calculateTime = 0;
    private TextView herbDetail,herbDetailTitle;   // 展示图片识别出的中药的描述结果，文心一言生成
    private MyHandler handler = new MyHandler(this);

    public MainFragment() {
    }

    public static MainFragment newInstance(String param1) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_NAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentName = getArguments().getString(FRAGMENT_NAME);
        }
        try {
            // 加载PyTorch模型
            model = Module.load(assetFilePath(getActivity(), "model.pt"));
        } catch (Exception e) {
            Log.e("MainFragment", "模型加载失败", e);
            Toast.makeText(getActivity(), "模型加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
        }

        selectedImage = rootView.findViewById(R.id.selected_image);
        LinearLayout selectImageButton = rootView.findViewById(R.id.frameLayout1);
        LinearLayout takePhotoButton = rootView.findViewById(R.id.frameLayout2);
        classificationResult = rootView.findViewById(R.id.classification_result);
        onLoading = rootView.findViewById(R.id.iv_loading);
        timeUsage = rootView.findViewById(R.id.classification_time);
        herbDetail = rootView.findViewById(R.id.herb_details_content);
        herbDetailTitle = rootView.findViewById(R.id.herb_details_title);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        return rootView;
    }




    private void selectImage() {
        // 选择图片
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                    if (selectedBitmap != null) {
                        selectedImage.setImageBitmap(selectedBitmap);
                        // 在子线程中进行分类,避免阻塞Ui线程
                        new ClassificationTask().execute(selectedBitmap);
                    } else {
                        Log.e("MainFragment", "解码Bitmap失败");
                        Toast.makeText(getActivity(), "解码Bitmap失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    Log.e("MainFragment", "图片选择失败", e);
                    Toast.makeText(getActivity(), "图片选择失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("MainFragment", "图片Uri为空");
                Toast.makeText(getActivity(), "图片Uri为空", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ClassificationTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            // 将图片缩放成224*224
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            // 将图片转换为tensor，并均值归一化
//            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap,
//                    TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap,
                    new float[]{0.5f, 0.5f, 0.5f}, new float[]{0.5f, 0.5f, 0.5f});
            // 使用模型进行推断
            long startTime = System.currentTimeMillis();
            Tensor outputTensor = model.forward(IValue.from(inputTensor)).toTensor();
            long endTime = System.currentTimeMillis();
            calculateTime = endTime - startTime;
            // 获取分类结果
            float[] scores = outputTensor.getDataAsFloatArray();
            int maxScoreIdx = -1;
            float maxScore = -Float.MAX_VALUE;
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    maxScoreIdx = i;
                }
            }
            return classes[maxScoreIdx];
        }
        @Override
        protected void onPreExecute() {
            classificationResult.setVisibility(View.GONE);
            onLoading.setVisibility(View.VISIBLE);
            // onLoading 一直执行旋转动画
            onLoading.setRotation(0);
            onLoading.animate().rotation(360).setDuration(2000).setInterpolator(new LinearInterpolator()).start();

        }
        @Override
        protected void onPostExecute(String result) {
            classificationResult.setVisibility(View.VISIBLE);
            onLoading.setVisibility(View.GONE);
            classificationResult.setText( result);
            timeUsage.setText(calculateTime + "ms");
            herbDetailTitle.setText(result);
            herbDetail.setText("描述生成中...");
            String question = "请给我介绍一下" + result+"这种中草药";
            // 使用子线程获取答案，然后用handler发送消息给主线程，在主线程更新
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WenXin wenXin = new WenXin();
                        String answer = wenXin.getAnswer(question);
                        Message message = new Message();
                        message.what = MSG_DETAIL;
                        message.obj = answer;
                        handler.sendMessage(message);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    // Helper function to load model file
    public static String assetFilePath(Activity activity, String assetName) throws IOException {
        File file = new File(activity.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = activity.getAssets().open(assetName)) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
            }
            return file.getAbsolutePath();
        }
    }

    private static class MyHandler extends Handler{

        private WeakReference<MainFragment> weakReference;
        public MyHandler(MainFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainFragment fragment = weakReference.get();
            if (fragment != null) {
                switch (msg.what){
                    case MSG_DETAIL:
                        String result = (String) msg.obj;
                        fragment.herbDetail.setText(result);
                        fragment.herbDetail.setVisibility(View.VISIBLE);
                        break;

                }
            }
        }
    }



}
