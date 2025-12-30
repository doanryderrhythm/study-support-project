package com.example.studysupportproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etLoginUsername, etLoginPassword;
    private Button btnLogin, btnCreateAccount;  // Thay đổi ở đây
    private TextView tvForgotPassword;
    private ImageView btnToggleLoginPassword;
    private DatabaseHelper dbHelper;

    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "=== LOGIN ACTIVITY STARTED ===");

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Ánh xạ view
        etLoginUsername = findViewById(R.id.loginUsernameEditText);
        etLoginPassword = findViewById(R.id.loginPasswordEditText);
        btnLogin = findViewById(R.id.loginButton);
        tvForgotPassword = findViewById(R.id.textViewForgotPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);  // ID mới
        btnToggleLoginPassword = findViewById(R.id.btnToggleLoginPassword);

        Log.d(TAG, "btnCreateAccount = " + btnCreateAccount);

        dbHelper = new DatabaseHelper();

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

        // Hiển thị loading
        showLoading(true);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Thực hiện login trên background thread
                    final User user = dbHelper.checkLogin(usernameOrEmail, password);

                    // Quay về main thread để update UI
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);

                            if (user != null) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);

                                // Lưu thông tin của user
                                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("user_id", user.getId());
                                editor.putString("role", user.getRole());
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Login error: " + e.getMessage());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);
                            Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            btnLogin.setEnabled(false);
            btnLogin.setText("Đang đăng nhập...");
        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText("Đăng nhập");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dọn dẹp executor
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}