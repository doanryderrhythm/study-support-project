package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private ImageView toggleNewPassword, toggleConfirmPassword;
    private Button resetPasswordButton;
    private DatabaseHelper dbHelper;
    private String email;

    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Lấy email từ Intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        // Kiểm tra email
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper();
        initViews();
        setupListeners();
    }

    private void initViews() {
        newPasswordEditText = findViewById(R.id.newResetPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmResetPasswordEditText);
        toggleNewPassword = findViewById(R.id.btnToggleNewResetPassword);
        toggleConfirmPassword = findViewById(R.id.btnToggleConfirmResetPassword);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        // XÓA DÒNG NÀY VÌ KHÔNG CÓ PROGRESSBAR TRONG XML
        // progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        // Toggle new password visibility
        toggleNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNewPasswordVisibility();
            }
        });

        // Toggle confirm password visibility
        toggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleConfirmPasswordVisibility();
            }
        });

        // Reset password button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // Back to login
        TextView backToLogin = findViewById(R.id.textViewBackToLoginFromReset);
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void toggleNewPasswordVisibility() {
        if (isNewPasswordVisible) {
            newPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleNewPassword.setImageResource(R.drawable.ic_visibility_off);
            isNewPasswordVisible = false;
        } else {
            newPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleNewPassword.setImageResource(R.drawable.ic_visibility);
            isNewPasswordVisible = true;
        }
        newPasswordEditText.setSelection(newPasswordEditText.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleConfirmPassword.setImageResource(R.drawable.ic_visibility_off);
            isConfirmPasswordVisible = false;
        } else {
            confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleConfirmPassword.setImageResource(R.drawable.ic_visibility);
            isConfirmPasswordVisible = true;
        }
        confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();


        if (!validateInputs(newPassword, confirmPassword)) {
            return;
        }

        // Hiển thị loading
        showLoading(true);

        // Cập nhật mật khẩu trong database
        boolean success = dbHelper.updatePassword(email, newPassword);

        // Giả lập delay network
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoading(false);

                if (success) {
                    handleResetSuccess();
                } else {
                    handleResetError();
                }
            }
        }, 1500);
    }

    private boolean validateInputs(String newPassword, String confirmPassword) {
        // Kiểm tra mật khẩu mới
        if (newPassword.isEmpty()) {
            newPasswordEditText.setError("Vui lòng nhập mật khẩu mới");
            newPasswordEditText.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            newPasswordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự");
            newPasswordEditText.requestFocus();
            return false;
        }

        // Kiểm tra mật khẩu xác nhận
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Vui lòng xác nhận mật khẩu");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Mật khẩu không khớp");
            confirmPasswordEditText.requestFocus();
            return false;
        }



        return true;
    }

    private boolean isPasswordStrong(String password) {
        // Kiểm tra độ mạnh mật khẩu cơ bản
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasMinLength = password.length() >= 6;

        return hasLetter && hasDigit && hasMinLength;
    }

    private void showLoading(boolean isLoading) {

        if (isLoading) {
            resetPasswordButton.setEnabled(false);
            resetPasswordButton.setText("Đang xử lý...");
        } else {
            resetPasswordButton.setEnabled(true);
            resetPasswordButton.setText("Đặt Lại Mật Khẩu");
        }
    }

    private void handleResetSuccess() {
        resetPasswordButton.setText("THÀNH CÔNG!");

        // KIỂM TRA FILE btn_success_bg.xml CÓ TỒN TẠI KHÔNG
        try {
            resetPasswordButton.setBackgroundResource(R.drawable.btn_success_bg);
        } catch (Exception e) {
            // Nếu file không tồn tại, chỉ đổi text
            e.printStackTrace();
        }

        resetPasswordButton.setEnabled(false);

        Toast.makeText(this, "Mật khẩu đã được đặt lại thành công!", Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToLogin();
            }
        }, 2000);
    }

    private void handleResetError() {
        Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();


        // resetPasswordButton.setError("Thất bại");

        // Enable retry after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resetPasswordButton.setEnabled(true);
                resetPasswordButton.setText("Thử lại");
            }
        }, 2000);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateToLogin();
    }
}