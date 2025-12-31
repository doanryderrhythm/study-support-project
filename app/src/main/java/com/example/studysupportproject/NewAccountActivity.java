package com.example.studysupportproject;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewAccountActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword, etFullName, etPhoneNumber, etDateOfBirth, etAddress;
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


        dbHelper = new DatabaseHelper();

        initializeViews();
        setUpListeners();

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
    private void setUpListeners() {
        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
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
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.usernameEditText);
        etEmail = findViewById(R.id.emailEditText);
        etPassword = findViewById(R.id.newPasswordEditText);
        etConfirmPassword = findViewById(R.id.reEnterPasswordEditText);
        etFullName = findViewById(R.id.fullNameEditText);
        etPhoneNumber = findViewById(R.id.phoneEditText);
        etDateOfBirth = findViewById(R.id.dobEditText);
        etAddress = findViewById(R.id.addressEditText);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        btnSignUp = findViewById(R.id.signUpButton);
        tvCreateAccount = findViewById(R.id.textViewCreateAccount);

        etDateOfBirth.setFocusable(false);
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
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

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

        if (dateOfBirth.isEmpty()) {
            etDateOfBirth.setError("Ngày sinh trống");
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
        long result = dbHelper.addUser(username, email, password, fullName, phoneNumber, dateOfBirth, address);

        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình đăng nhập
        } else {
            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
        }
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // Try to parse existing date if available
        if (!etDateOfBirth.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.setTime(sdf.parse(etDateOfBirth.getText().toString()));
            } catch (Exception e) {
                // Use current date
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDateOfBirth.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}