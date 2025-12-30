package com.example.studysupportproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private TextView tvUserEmail, tvUserPhone, tvUserFullName;
    private ImageView ivUserAvatar;
    private Button btnEditProfile, btnChangePassword, btnDeleteAccount, btnChangeAvatar;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private int currentUserId;
    private User currentUser;
    private Uri selectedImageUri;

    private ExecutorService executorService;
    private Handler mainHandler;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.d(TAG, "=== SETTINGS ACTIVITY STARTED ===");

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        setupImagePicker();

        initializeViews();
        dbHelper = new DatabaseHelper();

        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (currentUserId != -1) {
            loadUserInfo();
        }

        setupClickListeners();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        ivUserAvatar.setImageURI(uri);
                        uploadImageToFirebase();
                    }
                }
        );
    }

    private void uploadImageToFirebase() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        String filename = "images/" + currentUserId + "_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(filename);

        // Upload image
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);

        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d(TAG, "Upload progress: " + progress + "%");
        }).addOnSuccessListener(taskSnapshot -> {
            // Get download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d(TAG, "Image uploaded successfully: " + downloadUrl);

                // Update database
                updateAvatarInDatabase(downloadUrl);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                showLoading(false);
                Toast.makeText(this, "Lỗi lấy URL ảnh", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Upload failed: " + e.getMessage());
            showLoading(false);
            Toast.makeText(this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateAvatarInDatabase(String avatarUrl) {
        executorService.execute(() -> {
            try {
                boolean success = dbHelper.updateUserAvatar(currentUserId, avatarUrl);

                mainHandler.post(() -> {
                    showLoading(false);
                    if (success) {
                        Toast.makeText(this, "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                        currentUser.setAvatar(avatarUrl);

                        // Load new avatar
                        Glide.with(this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_avatar_placeholder)
                                .into(ivUserAvatar);
                    } else {
                        Toast.makeText(this, "Lỗi cập nhật ảnh đại diện trong database", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error updating avatar in database: " + e.getMessage());
                mainHandler.post(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Lỗi kết nối database", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBackSettings);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserFullName = findViewById(R.id.tvUserFullName);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
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

                                if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                                    Glide.with(SettingsActivity.this)
                                            .load(currentUser.getAvatar())
                                            .placeholder(R.drawable.ic_avatar_placeholder)
                                            .error(R.drawable.ic_avatar_placeholder)
                                            .into(ivUserAvatar);
                                }
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

        btnChangeAvatar.setOnClickListener(v -> {
            // Open image picker
            imagePickerLauncher.launch("image/*");
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