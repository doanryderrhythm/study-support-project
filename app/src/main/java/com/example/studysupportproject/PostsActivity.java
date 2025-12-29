package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsActivity extends AppCompatActivity {
    private RecyclerView rvPosts;
    private LinearLayout llEmptyState;
    private FloatingActionButton fabMain;
    private ImageButton btnBack;
    private LinearLayout fabMenuItems, fabCreatePost, fabViewProfile;
    private boolean isFabMenuOpen = false;
    private Animation fabRotateClockwise, fabRotateCounterClockwise;
    private Animation fabOpen, fabClose;
    private PostsAdapter postsAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        dbHelper = new DatabaseHelper();

        rvPosts = findViewById(R.id.rvPosts);
        llEmptyState = findViewById(R.id.llEmptyState);

        btnBack = findViewById(R.id.btnBackPosts);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

    private void setupFabMenu() {
        fabMain = findViewById(R.id.fabMain);
        fabMenuItems = findViewById(R.id.fabMenuItems);
        fabCreatePost = findViewById(R.id.fabCreatePost);
        fabViewProfile = findViewById(R.id.fabViewProfile);

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

        fabViewProfile.setOnClickListener(v -> {
            closeFabMenu();
            Intent intent = new Intent(this, ProfilePostsActivity.class);
            startActivity(intent);
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

    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter(new ArrayList<>(), post -> {
            Intent intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("user_id", post.getAuthorId());
            startActivity(intent);
        });

        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postsAdapter);
    }

    private void loadPosts() {
        List<Post> posts = dbHelper.getAllPosts();

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