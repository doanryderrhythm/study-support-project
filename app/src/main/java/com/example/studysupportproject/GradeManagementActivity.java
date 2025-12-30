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

public class GradeManagementActivity extends AppCompatActivity {
    private RecyclerView semesterRecyclerView;
    private SemesterAdapter semesterAdapter;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private ConSQL conSQL;
    private List<Semester> semesterList;

    private DatabaseHelper dbHelper;
    private int currentUserId;
    private ImageView ivProfilePicture;
    private TextView tvProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_management);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Grade Management");
        }

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        // Setup RecyclerView
        semesterRecyclerView = findViewById(R.id.semester_recycler_view);
        semesterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        conSQL = new ConSQL();

        setupMenuButton();
        setupNavigationViewMenu();
        loadTeacherSemesters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationViewMenu();
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
                Intent intent = new Intent(GradeManagementActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_posts) {
                Intent intent = new Intent(GradeManagementActivity.this, PostsActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_study) {
                // Stay in grade management
            } else if (itemId == R.id.menu_account) {
                Intent intent = new Intent(GradeManagementActivity.this, AccountMenuActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_logout) {
                SharedPrefManager.getInstance(GradeManagementActivity.this).logout();
                Intent intent = new Intent(GradeManagementActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void loadTeacherSemesters() {
        new Thread(() -> {
            try {
                int teacherId = SharedPrefManager.getInstance(this).getUser().getId();

                String query = "SELECT DISTINCT s.id, s.semester_name " +
                        "FROM semesters s " +
                        "INNER JOIN classes c ON s.id = c.semester_id " +
                        "INNER JOIN class_teachers ct ON c.id = ct.class_id " +
                        "WHERE ct.teacher_id = " + teacherId + " " +
                        "ORDER BY s.semester_name";

                semesterList = new ArrayList<>();
                List<String> semesterNames = new ArrayList<>();
                List<java.util.Map<String, String>> results = conSQL.executeQuery(query);

                if (results != null) {
                    for (java.util.Map<String, String> row : results) {
                        int semesterId = Integer.parseInt(row.getOrDefault("id", "0"));
                        String semesterName = row.getOrDefault("semester_name", "Unknown");
                        semesterList.add(new Semester(semesterId, semesterName));
                        semesterNames.add(semesterName);
                    }
                }

                runOnUiThread(() -> {
                    if (semesterNames.isEmpty()) {
                        Toast.makeText(GradeManagementActivity.this, "No semesters found", Toast.LENGTH_SHORT).show();
                    } else {
                        semesterAdapter = new SemesterAdapter(semesterNames, semesterName -> {
                            // Find semester id by name
                            int semesterId = 0;
                            for (Semester s : semesterList) {
                                if (s.getName().equals(semesterName)) {
                                    semesterId = s.getId();
                                    break;
                                }
                            }
                            Intent intent = new Intent(GradeManagementActivity.this, ClassListActivity.class);
                            intent.putExtra("semester_id", semesterId);
                            intent.putExtra("semester_name", semesterName);
                            startActivity(intent);
                        });
                        semesterRecyclerView.setAdapter(semesterAdapter);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(GradeManagementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
