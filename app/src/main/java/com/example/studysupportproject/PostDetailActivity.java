package com.example.studysupportproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private Button btnSendComment;

    private DatabaseHelper dbHelper;
    private CommentsAdapter commentsAdapter;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        dbHelper = new DatabaseHelper();

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId == -1) {
            Toast.makeText(this, "Error loading post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadPostDetails();
        loadComments();
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
                        tvAuthor.setText("By @" + post.getUsername());
                    }

                    tvPostDate.setText(formatDate(post.getCreatedAt()));
                } else {
                    Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
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
}