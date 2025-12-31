package com.example.studysupportproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
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
import androidx.appcompat.widget.Toolbar;

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
    private Button  btnChangePassword, btnDeleteAccount;
    private ImageButton  btnEditProfile, btnChangeAvatar;
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
        setupToolbar();
        dbHelper = new DatabaseHelper();

        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (currentUserId != -1) {
            loadUserInfo();
        }

        setupClickListeners();
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tài khoản");
        }
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(View.GONE);
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
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserFullName = findViewById(R.id.tvUserFullName);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
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
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_personal_info, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        EditText etFullName = dialogView.findViewById(R.id.dialog_personal_info_name_input);
        etFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");

        EditText etPhone = dialogView.findViewById(R.id.dialog_personal_info_phone_input);
        etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");


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
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);


        EditText etCurrentPassword = dialogView.findViewById(R.id.dialog_change_password_current_password_input);

        EditText etNewPassword = dialogView.findViewById(R.id.dialog_change_password_new_password_input);

        EditText etConfirmPassword = dialogView.findViewById(R.id.dialog_change_password_retype_new_password_input);


        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
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
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete_account, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        EditText etPassword = dialogView.findViewById(R.id.dialog_confirm_delete_account_current_password_input);
        Button btnConfirmDelete = new Button(this);
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

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#E74C3C"));
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
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}