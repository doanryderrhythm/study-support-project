package com.example.studysupportproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    private TextView tvPostTitle, tvPostContent, tvPostType, tvAuthor, tvPostDate;
    private TextView tvCommentCount, tvNoComments;
    private RecyclerView rvComments;
    private EditText etComment;
    private Button btnSendComment, btnEditPost;
    private DatabaseHelper dbHelper;
    private CommentsAdapter commentsAdapter;
    private int postId;
    private int postAuthorId;
    private int currentUserId;
    private String userRole;
    
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;

    private ImageView ivProfilePicture;
    private TextView tvProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        dbHelper = new DatabaseHelper();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId == -1) {
            Toast.makeText(this, "Lỗi tải bài viết", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup drawer and navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        initViews();
        setupMenuButton();
        setupNavigationViewMenu();
        setupRecyclerView();
        loadPostDetails();
        loadComments();
        setupEditAbility();

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });

        btnEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, CreatePostActivity.class);
                intent.putExtra("post_id", postId);
                startActivityForResult(intent, 50);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationViewMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50 && resultCode == RESULT_OK) {
            loadPostDetails();
            loadComments();
        }
    }

    private void setupEditAbility() {
        postAuthorId = getIntent().getIntExtra("user_id", -1);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        if (postAuthorId != currentUserId) {
            btnEditPost.setVisibility(View.GONE);
        }
        else btnEditPost.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        tvPostTitle = findViewById(R.id.tvPostTitle);
        tvPostContent = findViewById(R.id.tvPostContent);
        tvPostType = findViewById(R.id.tvPostType);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPostDate = findViewById(R.id.tvPostDate);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvNoComments = findViewById(R.id.tvNoComments);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnEditPost = findViewById(R.id.btnEditPost);
    }

    private void setupRecyclerView() {
        commentsAdapter = new CommentsAdapter(new ArrayList<>());
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentsAdapter);
    }

    private void loadPostDetails() {
        new Thread(() -> {
            Post post = dbHelper.getPostById(postId);

            runOnUiThread(() -> {
                if (post != null) {
                    tvPostTitle.setText(post.getTitle());
                    tvPostContent.setText(post.getContent());
                    tvPostType.setText(post.getPostType().toUpperCase());

                    if (post.getUsername() != null) {
                        tvAuthor.setText("bởi @" + post.getUsername());
                    }

                    tvPostDate.setText(formatDate(post.getCreatedAt()));
                } else {
                    Toast.makeText(this, "Không thể tìm được bài viết này.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void loadComments() {
        new Thread(() -> {
            List<Comment> comments = dbHelper.getCommentsByPostId(postId);

            runOnUiThread(() -> {
                if (comments.isEmpty()) {
                    tvNoComments.setVisibility(View.VISIBLE);
                    rvComments.setVisibility(View.GONE);
                } else {
                    tvNoComments.setVisibility(View.GONE);
                    rvComments.setVisibility(View.VISIBLE);
                    commentsAdapter.updateComments(comments);
                }
                tvCommentCount.setText("(" + comments.size() + ")");
            });
        }).start();
    }

    private void sendComment() {
        String commentText = etComment.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Bạn chưa viết bình luận!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để bình luận!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ngăn chặn việc gửi một comment hai lần
        btnSendComment.setEnabled(false);

        new Thread(() -> {
            long commentId = dbHelper.addComment(postId, currentUserId, commentText);

            runOnUiThread(() -> {
                btnSendComment.setEnabled(true);

                if (commentId > 0) {
                    etComment.setText("");
                    Toast.makeText(this, "Thêm bình luận thành công!", Toast.LENGTH_SHORT).show();
                    loadComments(); // Reload comments
                } else {
                    Toast.makeText(this, "Không thể thêm bình luận.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String formatDate(String dateString) {
        try {
            if (dateString.contains(".")) {
                dateString = dateString.substring(0, dateString.indexOf("."));
            }

            SimpleDateFormat inputFormat;
            if (dateString.contains("T")) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            }

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date != null ? date : new Date());
        } catch (Exception e) {
            return dateString;
        }
    }

    private void setupMenuButton() {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void loadAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this).load(avatarUrl).into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_avatar_placeholder);
        }
    }

    private void loadUserProfile() {
        dbHelper = new DatabaseHelper();
        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("user_id", -1);
        View headerView = navView.getHeaderView(0);
        ivProfilePicture = headerView.findViewById(R.id.ivProfilePicture);
        tvProfileName = headerView.findViewById(R.id.tvProfileName);

        if (currentUserId == -1) {
            tvProfileName.setText("Khách");
            return;
        }

        // Load user data in background thread
        new Thread(() -> {
            User user = dbHelper.getUserById(currentUserId);

            runOnUiThread(() -> {
                if (user != null) {
                    if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                        tvProfileName.setText(user.getFullName());
                    } else {
                        tvProfileName.setText(user.getUsername());
                    }

                    loadAvatar(user.getAvatar());
                } else {
                    tvProfileName.setText("Unknown user");
                }
            });
        }).start();
    }

    private void setupNavigationViewMenu() {
        loadUserProfile();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_home) {
                    Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_posts) {
                    getOnBackPressedDispatcher().onBackPressed();
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_study) {
                    Intent intent;
                    User savedUser = SharedPrefManager.getInstance(PostDetailActivity.this).getUser();
                    userRole = savedUser != null ? savedUser.getRole() : "student";
                    if (userRole != null) {
                        if (userRole.equals("teacher") || userRole.equals("admin")) {
                            intent = new Intent(PostDetailActivity.this, GradeManagementActivity.class);
                        } else {
                            intent = new Intent(PostDetailActivity.this, StudentGradesActivity.class);
                        }
                        startActivity(intent);
                    } else {
                        Toast.makeText(PostDetailActivity.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_account) {
                    Intent intent = new Intent(PostDetailActivity.this, AccountMenuActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_logout) {
                    SharedPrefManager.getInstance(PostDetailActivity.this).logout();
                    Intent intent = new Intent(PostDetailActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                }

                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}