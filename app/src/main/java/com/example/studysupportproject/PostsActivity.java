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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

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
    
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private String userRole;
    private ImageButton navHome, navStudy, navProfile;

    private int currentUserId;
    private ImageView ivProfilePicture;
    private TextView tvProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        dbHelper = new DatabaseHelper();

        rvPosts = findViewById(R.id.rvPosts);
        llEmptyState = findViewById(R.id.llEmptyState);
        
        // Toolbar and navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Community Posts");
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        setupMenuButton();
        setupNavigationViewMenu();
        setupFabMenu();
        setupRecyclerView();
        loadPosts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();
        setupNavigationViewMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 100 || requestCode == 50) && resultCode == RESULT_OK) {
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
                    Intent intent = new Intent(PostsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_posts) {
                    // Posts button - stay in PostsActivity
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_study) {
                    Intent intent;
                    User savedUser = SharedPrefManager.getInstance(PostsActivity.this).getUser();
                    userRole = savedUser != null ? savedUser.getRole() : "student";
                    if (userRole != null) {
                        if (userRole.equals("teacher") || userRole.equals("admin")) {
                            intent = new Intent(PostsActivity.this, GradeManagementActivity.class);
                        } else {
                            intent = new Intent(PostsActivity.this, StudentGradesActivity.class);
                        }
                        startActivity(intent);
                    } else {
                        Toast.makeText(PostsActivity.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_account) {
                    Intent intent = new Intent(PostsActivity.this, AccountMenuActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_logout) {
                    SharedPrefManager.getInstance(PostsActivity.this).logout();
                    Intent intent = new Intent(PostsActivity.this, LoginActivity.class);
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