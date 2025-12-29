package com.example.studysupportproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreatePostActivity extends AppCompatActivity {
    private TextView tvTitle;
    private EditText etPostTitle, etPostContent;
    private RadioGroup rgPostType, rgPrivacyType;
    private RadioButton rbGeneral, rbAnnouncement, rbGrade;
    private RadioButton rbPublic, rbPrivate;
    private Button btnCancel, btnPublish;

    private DatabaseHelper dbHelper;
    private int currentUserId;
    private int editPostId = -1; // -1 means creating new post
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        dbHelper = new DatabaseHelper();

        // Get current user ID
        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if editing existing post
        editPostId = getIntent().getIntExtra("post_id", -1);
        isEditMode = editPostId != -1;

        initViews();
        setupListeners();
        setupBackPressHandler();

        if (isEditMode) {
            loadPostForEditing();
        }
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        etPostTitle = findViewById(R.id.etPostTitle);
        etPostContent = findViewById(R.id.etPostContent);
        rgPostType = findViewById(R.id.rgPostType);
        rgPrivacyType = findViewById(R.id.rgPrivacyType);
        rbGeneral = findViewById(R.id.rbGeneral);
        rbAnnouncement = findViewById(R.id.rbAnnouncement);
        rbGrade = findViewById(R.id.rbGrade);
        rbPrivate = findViewById(R.id.rbPrivate);
        rbPublic = findViewById(R.id.rbPublic);
        btnCancel = findViewById(R.id.btnCancel);
        btnPublish = findViewById(R.id.btnPublish);

        // Update title if editing
        if (isEditMode) {
            tvTitle.setText("Sửa bài viết");
            btnPublish.setText("Cập nhật");
        }
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnPublish.setOnClickListener(v -> {
            if (isEditMode) {
                updatePost();
            } else {
                createPost();
            }
        });
    }

    private void loadPostForEditing() {
        new Thread(() -> {
            Post post = dbHelper.getPostById(editPostId);

            runOnUiThread(() -> {
                if (post != null) {
                    etPostTitle.setText(post.getTitle());
                    etPostContent.setText(post.getContent());

                    switch (post.getPostType().toLowerCase()) {
                        case "general":
                            rbGeneral.setChecked(true);
                            break;
                        case "announcement":
                            rbAnnouncement.setChecked(true);
                            break;
                        case "grade":
                            rbGrade.setChecked(true);
                            break;
                    }

                    if (post.isPublished()) {
                        rbPublic.setChecked(true);
                    }
                    else {
                        rbPrivate.setChecked(true);
                    }
                } else {
                    Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void createPost() {
        String title = etPostTitle.getText().toString().trim();
        String content = etPostContent.getText().toString().trim();
        String postType = getSelectedPostType();
        int privacyType = getSelectedPrivacy();

        if (title.isEmpty()) {
            etPostTitle.setError("Tiêu đề là bắt buộc");
            etPostTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            etPostContent.setError("Nội dung là bắt buộc");
            etPostContent.requestFocus();
            return;
        }

        // Chặn đăng một bài viết hai lần
        btnPublish.setEnabled(false);
        btnPublish.setText("Đăng");

        new Thread(() -> {
            long postId = dbHelper.createPost(title, content, currentUserId, postType, privacyType);

            runOnUiThread(() -> {
                btnPublish.setEnabled(true);
                btnPublish.setText("Đăng");

                if (postId > 0) {
                    Toast.makeText(this, "Đăng bài viết thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Không thể đăng bài viết", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void updatePost() {
        String title = etPostTitle.getText().toString().trim();
        String content = etPostContent.getText().toString().trim();
        String postType = getSelectedPostType();
        int privacyType = getSelectedPrivacy();

        if (title.isEmpty()) {
            etPostTitle.setError("Tiêu đề là bắt buộc");
            etPostTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            etPostContent.setError("Nội dung là bắt buộc");
            etPostContent.requestFocus();
            return;
        }

        btnPublish.setEnabled(false);
        btnPublish.setText("Cập nhật");

        new Thread(() -> {
            boolean success = dbHelper.updatePost(editPostId, title, content, postType, privacyType);

            runOnUiThread(() -> {
                btnPublish.setEnabled(true);
                btnPublish.setText("Cập nhật");

                if (success) {
                    Toast.makeText(this, "Cập nhật bài viết thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Không thể cập nhật bài viết", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String getSelectedPostType() {
        int selectedId = rgPostType.getCheckedRadioButtonId();

        if (selectedId == R.id.rbAnnouncement) {
            return "announcement";
        } else if (selectedId == R.id.rbGrade) {
            return "grade";
        } else if (selectedId == R.id.rbGeneral) {
            return "general";
        }

        return "";
    }

    private int getSelectedPrivacy() {
        int selectedId = rgPrivacyType.getCheckedRadioButtonId();

        if (selectedId == R.id.rbPublic) {
            return 1;
        } else if (selectedId == R.id.rbPrivate) {
            return 0;
        }

        return -1;
    }
}