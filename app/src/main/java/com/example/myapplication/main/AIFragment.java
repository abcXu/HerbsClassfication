package com.example.myapplication.main;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.example.myapplication.WenXin;
import com.example.myapplication.adapters.MsgAdapter;
import com.example.myapplication.entity.Msg;
import java.util.ArrayList;
import java.util.List;

public class AIFragment extends Fragment {

    private static final String FRAGMENT_NAME = "fragmentName";
    private View rootView;
    private String mFragmentName;
    private ImageView btn_send;
    private EditText input_text;
    private List<Msg> msgList;
    private MsgAdapter adapter;
    private RecyclerView recyclerView;
    private Handler handler;

    public AIFragment() {
        // Required empty public constructor
    }

    public static AIFragment newInstance(String name) {
        AIFragment fragment = new AIFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_ai, container, false);
        }

        recyclerView = rootView.findViewById(R.id.msg_recycler_view);
        input_text = rootView.findViewById(R.id.input_text);
        btn_send = rootView.findViewById(R.id.send);
        btn_send.setEnabled(false);

        msgList = new ArrayList<>();
        adapter = new MsgAdapter(msgList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        input_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0){
                    btn_send.setImageResource(R.drawable.not_send);
                    btn_send.setEnabled(false);  // 输入框没有消息不能点击发送
                }else if (s.length() > 0) {
                    btn_send.setEnabled(true);
                    btn_send.setImageResource(R.drawable.can_send);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btn_send.setEnabled(true);
                    btn_send.setImageResource(R.drawable.can_send);
                } else {
                    btn_send.setEnabled(false);
                    btn_send.setImageResource(R.drawable.not_send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 不需要处理
            }
        });


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String answer = (String) msg.obj;
                    msgList.add(new Msg(answer, Msg.TYPE_RECEIVED));
                    adapter.notifyItemInserted(msgList.size() - 1);
                    recyclerView.scrollToPosition(msgList.size() - 1);


                }
            }
        };

        btn_send.setOnClickListener(v -> sendMessage());

        return rootView;
    }

    private void sendMessage() {
        String user_ask = input_text.getText().toString();
        input_text.setText("");
        if (user_ask.equals("")) {
            Toast.makeText(getContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        btn_send.setEnabled(false);   // 发送消息后禁用按钮
        btn_send.setImageResource(R.drawable.not_send);
        Msg msg = new Msg(user_ask, Msg.TYPE_SENT);
        msgList.add(msg);
        adapter.notifyItemInserted(msgList.size() - 1);
        recyclerView.scrollToPosition(msgList.size() - 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String answer = null;
                try {
                    WenXin wx = new WenXin();
                    answer = wx.getAnswer(user_ask);
                } catch (Exception e) {
                    e.printStackTrace();
                    answer = "获取回答失败";
                }
                Message message = handler.obtainMessage(1, answer);
                handler.sendMessage(message);
            }
        }).start();
    }

}
