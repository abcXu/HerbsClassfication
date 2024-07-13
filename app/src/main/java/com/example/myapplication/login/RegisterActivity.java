package com.example.myapplication.login;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {
    private Context context = this;
    private TextView tv_loginError;
    private LinearLayout lv_loginBox;
    private EditText et_account;
    private EditText et_password;
    private EditText et_password2;
    private ImageView btn_login;
    private Button btn_register;

    private String account = "";
    private String password = "";
    private String password2 = "";

    private DatabaseHelper dbHelper;

    private ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 初始化布局
        initView();
        // 注册账号
        register();
    }

    private void initView() {
        tv_loginError = findViewById(R.id.tv_loginError);
        lv_loginBox = findViewById(R.id.lv_LoginBox);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);
        et_password2 = findViewById(R.id.et_password2);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        });
    }

    private void register() {
        // 创建数据库操作对象
        dbHelper = new DatabaseHelper(this);
        // 注册按钮点击事件
        btn_register.setOnClickListener(v -> {
            account = et_account.getText().toString();
            password = et_password.getText().toString();
            password2 = et_password2.getText().toString();
            // 检查输入的合法性
            if (account.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                loginError("请规范输入用户名和密码");
                return;
            }
            // 检查用户名长度的合法性
            if (account.length() < 4) {
                loginError("用户名至少输入4个字符");
                return;
            }
            // 检查密码长度的合法性
            if (password.length() < 6) {
                loginError("密码至少输入6个字符");
                return;
            }
            // 检查两次密码是否相等
            if (!password.equals(password2)) {
                loginError("两次密码不一致！");
                return;
            }
            // 检查用户名是否已存在
            if (!dbHelper.checkUsername(account)) {
                // 注册
                boolean isInserted = dbHelper.addUser(account, password);
                if (isInserted) {
                    // 注册成功
                    showToast("注册成功！");
                    tv_loginError.setText("");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                            });
                        }
                    }, 500);
                } else {
                    showToast("注册失败！");
                }
            } else {
                loginError("用户名已存在！");
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private void loginError(String msg) {
        tv_loginError.setText(msg);
        objectAnimator = ObjectAnimator.ofFloat(tv_loginError,
                "translationX", 0f, 50f, -50f, 0f);
        objectAnimator.setDuration(850);
        objectAnimator.setRepeatCount(0);
        objectAnimator.start();

        objectAnimator = ObjectAnimator.ofFloat(lv_loginBox,
                "translationX", 0f, 30f, -30f, 0f);
        objectAnimator.setDuration(700);
        objectAnimator.start();
    }
}
