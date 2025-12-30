package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnResetPassword;
    private TextView tvBackToLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);


        etEmail = findViewById(R.id.forgotEmailEditText);
        btnResetPassword = findViewById(R.id.resetPasswordButton);
        tvBackToLogin = findViewById(R.id.textViewBackToLogin);

        dbHelper = new DatabaseHelper();

        // Sự kiện gửi yêu cầu đặt lại mật khẩu
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetLink();
            }
        });

        // Sự kiện quay lại Login
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendResetLink() {
        String email = etEmail.getText().toString().trim();

        // Kiểm tra đầu vào
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        // Kiểm tra định dạng email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+";
        if (!email.matches(emailPattern)) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }


        btnResetPassword.setText("Đang xử lý...");
        btnResetPassword.setEnabled(false);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Kiểm tra email có tồn tại trong database không
                                boolean emailExists = dbHelper.checkEmailExists(email);

                                if (!emailExists) {
                                    btnResetPassword.setText("Gửi Liên Kết Đặt Lại");
                                    btnResetPassword.setEnabled(true);
                                    Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Tạo mã OTP ngẫu nhiên 6 số
                                String otp = generateOTP();

                                Toast.makeText(ForgotPasswordActivity.this, "Mã OTP đã được gửi đến " + email, Toast.LENGTH_LONG).show();

                                // Hiển thị OTP trong log cho mục đích demo
                                // (ĐÃ XÓA BuildConfig.DEBUG để tránh lỗi)
                                Log.d("otp", otp);

                                // Chuyển sang màn hình xác thực OTP
                                Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("otp", otp);
                                startActivity(intent);

                                // Khôi phục nút
                                btnResetPassword.setText("Gửi Liên Kết Đặt Lại");
                                btnResetPassword.setEnabled(true);
                            }
                        });
                    }
                },
                1500);
    }

    private String generateOTP() {
        int otpNumber = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otpNumber);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}