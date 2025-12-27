package com.example.studysupportproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostsActivity extends AppCompatActivity {
    private ImageView imgProfilePicture;
    private TextView tvFullName, tvUsername;
    private RecyclerView rvPosts;
    private LinearLayout llEmptyState;
    private PostsAdapter postsAdapter;
    private FloatingActionButton fabMain;
    private LinearLayout fabMenuItems, fabCreatePost;
    private boolean isFabMenuOpen = false;
    private Animation fabRotateClockwise, fabRotateCounterClockwise;
    private Animation fabOpen, fabClose;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_posts);

        dbHelper = new DatabaseHelper();

        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        tvFullName = findViewById(R.id.tvFullName);
        tvUsername = findViewById(R.id.tvUsername);

        rvPosts = findViewById(R.id.rvPosts);
        llEmptyState = findViewById(R.id.llEmptyState);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        setupUserInfo();
        setupFabMenu();
        setupRecyclerView();
        loadPosts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadPosts();
        }
    }

    private void setupUserInfo()
    {
        User currentUser = dbHelper.getUserById(currentUserId);
        if (currentUser != null) {
            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                Glide.with(this.getApplicationContext())
                        .load(currentUser.getAvatar())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(imgProfilePicture);
            }
            tvFullName.setText(currentUser.getFullName());
            tvUsername.setText(currentUser.getUsername());
        }
    }

    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter(new ArrayList<>(), post -> {
            Intent intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            startActivity(intent);
        });

        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postsAdapter);
    }

    private void setupFabMenu() {
        fabMain = findViewById(R.id.fabMain);
        fabMenuItems = findViewById(R.id.fabMenuItems);
        fabCreatePost = findViewById(R.id.fabCreatePost);

        fabRotateClockwise = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_clockwise);
        fabRotateCounterClockwise = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_counterclockwise);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        fabMain.setOnClickListener(v -> toggleFabMenu());

        fabCreatePost.setOnClickListener(v -> {
            closeFabMenu();
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivityForResult(intent, 100);
        });
    }

    private void toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu();
        } else {
            openFabMenu();
        }
    }

    private void openFabMenu() {
        fabMenuItems.setVisibility(View.VISIBLE);

        fabMain.startAnimation(fabRotateClockwise);
        fabMenuItems.startAnimation(fabOpen);

        isFabMenuOpen = true;
    }

    private void closeFabMenu() {
        fabMain.startAnimation(fabRotateCounterClockwise);
        fabMenuItems.startAnimation(fabClose);

        fabMenuItems.postDelayed(() -> {
            fabMenuItems.setVisibility(View.GONE);
        }, 200);

        isFabMenuOpen = false;
    }

    private void loadPosts() {
        List<Post> posts = dbHelper.getPostsFromAuthor(currentUserId);

        if (posts.isEmpty()) {
            rvPosts.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvPosts.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
            postsAdapter.updatePosts(posts);
        }
    }
}