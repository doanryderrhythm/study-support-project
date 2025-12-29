package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import android.view.MenuItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View navPost;
    private View navStudy;
    private View navProfile;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;

    private ImageView ivProfilePicture;
    private TextView tvProfileName;

    private RecyclerView rvSchedules;
    private TextView tvEmptyState;
    private ScheduleAdapter scheduleAdapter;

    private DatabaseHelper dbHelper;
    private int currentUserId;
    private String userRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        dbHelper = new DatabaseHelper();

        setContentView(R.layout.activity_main);

        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        // Get user role from database
        new Thread(() -> {
            User currentUser = dbHelper.getUserById(currentUserId);
            userRole = currentUser != null ? currentUser.getRole() : "student";
            Log.d("MainActivity", "User role from database: " + userRole);
            
            // Update SharedPrefManager with role if not already set
            User savedUser = SharedPrefManager.getInstance(MainActivity.this).getUser();
            if (savedUser != null && (savedUser.getRole() == null || savedUser.getRole().isEmpty())) {
                savedUser.setRole(userRole);
                SharedPrefManager.getInstance(MainActivity.this).userLogin(savedUser);
            }
        }).start();

        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navPost = findViewById(R.id.nav_post);
        navStudy = findViewById(R.id.nav_study);
        navProfile = findViewById(R.id.nav_profile);
        navView = findViewById(R.id.nav_view);
        
        rvSchedules = findViewById(R.id.rv_schedules);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        
        View headerView = navView.getHeaderView(0);
        ivProfilePicture = headerView.findViewById(R.id.ivProfilePicture);
        tvProfileName = headerView.findViewById(R.id.tvProfileName);

        loadUserProfile();
        setupSchedulesRecyclerView();
        setupMenuButton();
        setupNavigationViewMenu();
        setupBottomNavigation();
        loadSchedules();
    }

    private void loadUserProfile() {
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

    private void loadAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this).load(avatarUrl).into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_avatar_placeholder);
        }
    }

    private void setupSchedulesRecyclerView() {
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(null, this);

        // Set click listener for schedule items
        scheduleAdapter.setOnScheduleClickListener(schedule -> {
            Toast.makeText(MainActivity.this,
                    "Schedule: " + schedule.getTitle(),
                    Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Schedule clicked: " + schedule.toString());
        });

        rvSchedules.setAdapter(scheduleAdapter);
    }

    private void loadSchedules() {
        if (currentUserId == -1) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
            return;
        }

        // Load schedules in background thread
        new Thread(() -> {
            List<Schedule> schedules = dbHelper.getSchedulesForUser(currentUserId);

            runOnUiThread(() -> {
                if (schedules != null && !schedules.isEmpty()) {
                    scheduleAdapter.updateList(schedules);
                    rvSchedules.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                    Log.i("MainActivity", "Loaded " + schedules.size() + " schedules");
                } else {
                    rvSchedules.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                    Log.i("MainActivity", "No schedules found for user " + currentUserId);
                }
            });
        }).start();
    }

    private void setupMenuButton() {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void setupNavigationViewMenu() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_home) {
                    // Home button - stay in MainActivity
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_posts) {
                    Intent intent = new Intent(MainActivity.this, PostsActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_study) {
                    Intent intent;
                    if (userRole != null) {
                        if (userRole.equals("teacher") || userRole.equals("admin")) {
                            intent = new Intent(MainActivity.this, GradeManagementActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, StudentGradesActivity.class);
                        }
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_profile) {
                    Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    // TODO: Implement profile activity
                } else if (itemId == R.id.nav_account) {
                    Intent intent = new Intent(MainActivity.this, AccountMenuActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                }

                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    private void setupBottomNavigation() {
        navPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });

        navStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentGradesActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountMenuActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });

    }
}