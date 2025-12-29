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

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import android.view.MenuItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private View navHome;
    private View navStudy;
    private View navProfile;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;

    private ImageView ivProfilePicture;
    private TextView tvProfileName;

    private DatabaseHelper dbHelper;
    private int currentUserId;
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

        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navHome = findViewById(R.id.nav_home);
        navStudy = findViewById(R.id.nav_study);
        navProfile = findViewById(R.id.nav_profile);
        navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        ivProfilePicture = headerView.findViewById(R.id.ivProfilePicture);
        tvProfileName = headerView.findViewById(R.id.tvProfileName);

        loadUserProfile();
        setupMenuButton();
        setupBottomNavigation();
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

    private void setupMenuButton() {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
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
                Toast.makeText(MainActivity.this, "Màn hình học tập", Toast.LENGTH_SHORT).show();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Màn hình hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_account) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                    return true;
                }

                return false;
            }
        });
    }
}