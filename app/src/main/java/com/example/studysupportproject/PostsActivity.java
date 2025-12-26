package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsActivity extends AppCompatActivity {
    private RecyclerView rvPosts;
    private LinearLayout llEmptyState;
    private PostsAdapter postsAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        dbHelper = new DatabaseHelper();

        rvPosts = findViewById(R.id.rvPosts);
        llEmptyState = findViewById(R.id.llEmptyState);

        setupRecyclerView();
        loadPosts();
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