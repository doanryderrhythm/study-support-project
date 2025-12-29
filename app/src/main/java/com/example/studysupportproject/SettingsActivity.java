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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private TextView tvUserEmail, tvUserPhone, tvUserFullName;
    private Button btnEditProfile, btnChangePassword, btnDeleteAccount;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private int currentUserId;
    private User currentUser;

    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.d(TAG, "=== SETTINGS ACTIVITY STARTED ===");

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initializeViews();
        dbHelper = new DatabaseHelper();

        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (currentUserId != -1) {
            loadUserInfo();
        }

        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBackSettings);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserFullName = findViewById(R.id.tvUserFullName);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
    }

    private void loadUserInfo() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    currentUser = dbHelper.getUserById(currentUserId);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (currentUser != null) {
                                tvUserFullName.setText(currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()
                                        ? currentUser.getFullName() : currentUser.getUsername());
                                tvUserEmail.setText(currentUser.getEmail());
                                tvUserPhone.setText(currentUser.getPhone() != null && !currentUser.getPhone().isEmpty()
                                        ? currentUser.getPhone() : "Chưa cập nhật");
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error loading user info: " + e.getMessage());
                }
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountConfirmation();
            }
        });
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin cá nhân");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 10, 20, 10);

        EditText etFullName = new EditText(this);
        etFullName.setHint("Họ và tên");
        etFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
        etFullName.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        layout.addView(etFullName);

        EditText etPhone = new EditText(this);
        etPhone.setHint("Số điện thoại");
        etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        layout.addView(etPhone);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newFullName = etFullName.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();

            if (newFullName.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserProfile(newFullName, newPhone);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void updateUserProfile(String fullName, String phone) {
        showLoading(true);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = dbHelper.updateUserProfile(currentUser.getEmail(), fullName, phone);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);

                            if (success) {
                                Toast.makeText(SettingsActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                currentUser.setFullName(fullName);
                                currentUser.setPhone(phone);
                                tvUserFullName.setText(fullName);
                                tvUserPhone.setText(phone);
                            } else {
                                Toast.makeText(SettingsActivity.this, "Lỗi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error updating profile: " + e.getMessage());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);
                            Toast.makeText(SettingsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 10, 20, 10);

        EditText etCurrentPassword = new EditText(this);
        etCurrentPassword.setHint("Mật khẩu hiện tại");
        etCurrentPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etCurrentPassword);

        EditText etNewPassword = new EditText(this);
        etNewPassword.setHint("Mật khẩu mới");
        etNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPassword);

        EditText etConfirmPassword = new EditText(this);
        etConfirmPassword.setHint("Xác nhận mật khẩu mới");
        etConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirmPassword);

        builder.setView(layout);

        builder.setPositiveButton("Đổi", (dialog, which) -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!currentPassword.equals(currentUser.getPassword())) {
                Toast.makeText(SettingsActivity.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(SettingsActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(SettingsActivity.this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            updatePassword(newPassword);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void updatePassword(String newPassword) {
        showLoading(true);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = dbHelper.updatePassword(currentUser.getEmail(), newPassword);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);

                            if (success) {
                                Toast.makeText(SettingsActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                currentUser.setPassword(newPassword);
                            } else {
                                Toast.makeText(SettingsActivity.this, "Lỗi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error updating password: " + e.getMessage());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);
                            Toast.makeText(SettingsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showDeleteAccountConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa tài khoản");
        builder.setMessage("Bạn chắc chắn muốn xóa tài khoản không? Hành động này không thể hoàn tác.");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            showDeleteAccountPasswordDialog();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void showDeleteAccountPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận mật khẩu");
        builder.setMessage("Vui lòng nhập mật khẩu để xóa tài khoản");

        EditText etPassword = new EditText(this);
        etPassword.setHint("Mật khẩu");
        etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(etPassword);

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            String password = etPassword.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(currentUser.getPassword())) {
                Toast.makeText(SettingsActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            deleteAccount();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void deleteAccount() {
        showLoading(true);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = dbHelper.deleteUser(currentUser.getEmail());

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);

                            if (success) {
                                Toast.makeText(SettingsActivity.this, "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();

                                // Xóa SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                prefs.edit().clear().apply();
                                SharedPrefManager.getInstance(SettingsActivity.this).logout();

                                // Quay về LoginActivity
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Lỗi xóa tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting account: " + e.getMessage());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoading(false);
                            Toast.makeText(SettingsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showLoading(boolean isLoading) {
        btnEditProfile.setEnabled(!isLoading);
        btnChangePassword.setEnabled(!isLoading);
        btnDeleteAccount.setEnabled(!isLoading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}