package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class ClassListActivity extends AppCompatActivity {
    private RecyclerView classRecyclerView;
    private ClassAdapter classAdapter;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private ConSQL conSQL;
    private String semesterName;
    private int semesterId;

    private DatabaseHelper dbHelper;
    private int currentUserId;
    private ImageView ivProfilePicture;
    private TextView tvProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        semesterId = getIntent().getIntExtra("semester_id", -1);
        semesterName = getIntent().getStringExtra("semester_name");

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Classes - " + semesterName);
        }

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        // Setup RecyclerView
        classRecyclerView = findViewById(R.id.class_recycler_view);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        conSQL = new ConSQL();

        setupMenuButton();
        setupNavigationViewMenu();
        loadClassesForSemester();
    }

    private void setupMenuButton() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
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
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(ClassListActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_posts) {
                Intent intent = new Intent(ClassListActivity.this, PostsActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_study) {
                getOnBackPressedDispatcher().onBackPressed();
            } else if (itemId == R.id.menu_account) {
                Intent intent = new Intent(ClassListActivity.this, AccountMenuActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_logout) {
                SharedPrefManager.getInstance(ClassListActivity.this).logout();
                Intent intent = new Intent(ClassListActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void loadClassesForSemester() {
        new Thread(() -> {
            try {
                int teacherId = SharedPrefManager.getInstance(this).getUser().getId();

                String query = "SELECT c.id, c.class_name " +
                        "FROM classes c " +
                        "INNER JOIN class_teachers ct ON c.id = ct.class_id " +
                        "WHERE ct.teacher_id = " + teacherId + " " +
                        "AND c.semester_id = " + semesterId + " " +
                        "ORDER BY c.class_name";

                java.util.List<java.util.Map<String, String>> results = conSQL.executeQuery(query);
                java.util.List<java.util.Map<String, Object>> classesWithIds = new java.util.ArrayList<>();

                if (results != null) {
                    for (java.util.Map<String, String> row : results) {
                        java.util.Map<String, Object> classItem = new java.util.HashMap<>();
                        classItem.put("id", Integer.parseInt(row.getOrDefault("id", "0")));
                        classItem.put("name", row.getOrDefault("class_name", "Unknown"));
                        classesWithIds.add(classItem);
                    }
                }

                runOnUiThread(() -> {
                    if (classesWithIds.isEmpty()) {
                        Toast.makeText(ClassListActivity.this, "No classes found", Toast.LENGTH_SHORT).show();
                    } else {
                        classAdapter = new ClassAdapter(classesWithIds, classItem -> {
                            Intent intent = new Intent(ClassListActivity.this, StudentGradesEditActivity.class);
                            intent.putExtra("semester_name", semesterName);
                            intent.putExtra("class_name", (String) classItem.get("name"));
                            intent.putExtra("class_id", (int) classItem.get("id"));
                            startActivity(intent);
                        });
                        classRecyclerView.setAdapter(classAdapter);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(ClassListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
