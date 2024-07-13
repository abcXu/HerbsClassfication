package com.example.myapplication.login;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.main.MainActivity;
import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {
    private Context context = this;
    private TextView tv_loginError;
    private LinearLayout lv_loginBox;
    private EditText et_account;
    private EditText et_password;
    private CheckBox cb_accept;
    private Button btn_login;
    private ImageView btn_register;
    private ImageView btn_help;

    private String account = "";
    private String password = "";
    private boolean acceptState = false;

    private ObjectAnimator objectAnimator;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        login();
    }

    private void initView() {
        tv_loginError = findViewById(R.id.tv_loginError);
        lv_loginBox = findViewById(R.id.lv_LoginBox);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);
        cb_accept = findViewById(R.id.cb_accept);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_help = findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("有事靠自己！");
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegisterActivity.class));
                finish();
            }
        });
    }

    private void login() {
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
            return;
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptState = cb_accept.isChecked();
                account = et_account.getText().toString();
                password = et_password.getText().toString();
                if (account.length() == 0 || password.length() == 0) {
                    loginError("账号和密码不能为空");
                    return;
                }
                if (!acceptState) {
                    loginError("请同意协议");
                    return;
                }

                if (dbHelper.checkUser(account, password)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("account", account);
                    editor.apply();
                    showToast("登录成功！");
                    tv_loginError.setText("");
                    startActivity(new Intent(context, MainActivity.class));
                    finish();

                } else {
                    loginError("用户名或密码错误!");
                }
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
