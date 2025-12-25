package com.example.studysupportproject;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewAccountActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private ImageView btnTogglePassword, btnToggleConfirmPassword;
    private Button btnSignUp;
    private TextView tvCreateAccount;
    private DatabaseHelper dbHelper;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_account);

        // Ánh xạ các view - SỬA THEO ID TRONG LAYOUT
        etUsername = findViewById(R.id.usernameEditText);
        etEmail = findViewById(R.id.emailEditText);
        etPassword = findViewById(R.id.newPasswordEditText);
        etConfirmPassword = findViewById(R.id.reEnterPasswordEditText);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        btnSignUp = findViewById(R.id.signUpButton);
        tvCreateAccount = findViewById(R.id.textViewCreateAccount); // ID ĐÚNG

        dbHelper = new DatabaseHelper();

        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleConfirmPasswordVisibility();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });


        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình đăng nhập
            }
        });


        try {
            View mainView = findViewById(R.id.main);
            if (mainView != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }
        } catch (Exception e) {
            // Không có id main trong layout, bỏ qua
        }
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Ẩn mật khẩu
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            passwordVisible = false;
        } else {
            // Hiện mật khẩu
            etPassword.setTransformationMethod(null);
            btnTogglePassword.setImageResource(R.drawable.ic_visibility);
            passwordVisible = true;
        }
        // Di chuyển cursor về cuối
        etPassword.setSelection(etPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (confirmPasswordVisible) {
            // Ẩn mật khẩu
            etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility_off);
            confirmPasswordVisible = false;
        } else {
            // Hiện mật khẩu
            etConfirmPassword.setTransformationMethod(null);
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility);
            confirmPasswordVisible = true;
        }
        // Di chuyển cursor về cuối
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
    }

    private void signUpUser() {
        // Lấy dữ liệu từ các trường nhập
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return;
        }

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

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // Kiểm tra username đã tồn tại chưa
        if (dbHelper.checkUsernameExists(username)) {
            etUsername.setError("Tên đăng nhập đã được sử dụng");
            etUsername.requestFocus();
            return;
        }

        // Kiểm tra email đã tồn tại chưa
        if (dbHelper.checkEmailExists(email)) {
            etEmail.setError("Email đã được đăng ký");
            etEmail.requestFocus();
            return;
        }

        // Thêm người dùng vào database
        long result = dbHelper.addUser(username, email, password);

        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình đăng nhập
        } else {
            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}