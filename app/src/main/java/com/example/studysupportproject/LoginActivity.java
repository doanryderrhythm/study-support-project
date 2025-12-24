package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etLoginUsername, etLoginPassword;
    private Button btnLogin, btnCreateAccount;  // Thay đổi ở đây
    private TextView tvForgotPassword;
    private ImageView btnToggleLoginPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "=== LOGIN ACTIVITY STARTED ===");

        // Ánh xạ view
        etLoginUsername = findViewById(R.id.loginUsernameEditText);
        etLoginPassword = findViewById(R.id.loginPasswordEditText);
        btnLogin = findViewById(R.id.loginButton);
        tvForgotPassword = findViewById(R.id.textViewForgotPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);  // ID mới
        btnToggleLoginPassword = findViewById(R.id.btnToggleLoginPassword);

        Log.d(TAG, "btnCreateAccount = " + btnCreateAccount);

        dbHelper = new DatabaseHelper(this);

        // Sự kiện đăng ký
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Create account Button CLICKED!");

                try {
                    Intent intent = new Intent(LoginActivity.this, NewAccountActivity.class);
                    Log.d(TAG, "Intent created: " + intent);
                    startActivity(intent);
                    Log.d(TAG, "Activity started successfully!");
                } catch (Exception e) {
                    Log.e(TAG, "Error starting activity: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        );

        //  đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // quên mật khẩu
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Forgot password clicked");
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        //  toggle password
        btnToggleLoginPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
        // TEST: Tự động chuyển sau 2 giây
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Auto-test: Starting NewAccountActivity");
                Intent intent = new Intent(LoginActivity.this, NewAccountActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }

    private void togglePasswordVisibility() {

        if (etLoginPassword.getTransformationMethod() == null) {
            etLoginPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
            btnToggleLoginPassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            etLoginPassword.setTransformationMethod(null);
            btnToggleLoginPassword.setImageResource(R.drawable.ic_visibility);
        }
        etLoginPassword.setSelection(etLoginPassword.getText().length());
    }

    private void loginUser() {
        String usernameOrEmail = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.checkLogin(usernameOrEmail, password);
        if (user != null) {
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            SharedPrefManager.getInstance(this).userLogin(user);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}