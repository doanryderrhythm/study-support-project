package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentGradesActivity extends AppCompatActivity {
    private RecyclerView gradesRecyclerView;
    private GradesAdapter gradesAdapter;
    private ConSQL conSQL;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;

    private DatabaseHelper dbHelper;
    private int currentUserId;
    private ImageView ivProfilePicture;
    private TextView tvProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_grades);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Toolbar and navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Grades");
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);

        initializeViews();
        loadStudentGrades();
        setupMenuButton();
        setupNavigationViewMenu();
    }

    private void initializeViews() {
        gradesRecyclerView = findViewById(R.id.grades_recycler_view);
        gradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conSQL = new ConSQL();
    }

    private void loadStudentGrades() {
        // Get current user ID from SharedPrefManager
        int userId = SharedPrefManager.getInstance(this).getUser().getId();

        // Fetch grades from database
        List<Grade> allGrades = new ArrayList<>();
        List<String> semesters = new ArrayList<>();
        Map<String, Map<String, List<Grade>>> gradesBySemesterAndClass = new HashMap<>();

        try {
            // Query: SELECT grades with semester information for current student
            String query = "SELECT g.id, g.student_id, g.class_id, " +
                    "g.grade_value, g.grade_type, g.notes, " +
                    "c.semester_id, c.class_name, s.semester_name, u.full_name as teacher_name, " +
                    "g.created_at, g.updated_at " +
                    "FROM grades g " +
                    "INNER JOIN classes c ON g.class_id = c.id " +
                    "LEFT JOIN semesters s ON c.semester_id = s.id " +
                    "LEFT JOIN class_teachers ct ON c.id = ct.class_id " +
                    "LEFT JOIN users u ON ct.teacher_id = u.id " +
                    "WHERE g.student_id = " + userId + " " +
                    "ORDER BY c.semester_id DESC, c.class_name ASC";


            List<Map<String, String>> result = conSQL.executeQuery(query);

            if (result != null && !result.isEmpty()) {
                for (Map<String, String> row : result) {
                    // Create grade using class name instead of subject_name
                    Grade grade = new Grade(
                            Integer.parseInt(row.getOrDefault("id", "0")),
                            Integer.parseInt(row.getOrDefault("student_id", "0")),
                            Integer.parseInt(row.getOrDefault("class_id", "0")),
                            row.getOrDefault("class_name", ""), // Use class_name instead of subject_name
                            Double.parseDouble(row.getOrDefault("grade_value", "0")),
                            row.getOrDefault("grade_type", ""),
                            Integer.parseInt(row.getOrDefault("semester_id", "0")),
                            row.getOrDefault("semester_name", "Unknown Semester"),
                            "", // school_year removed from schema, use empty string
                            0, // teacherId not in grades table anymore
                            row.getOrDefault("notes", ""),
                            row.getOrDefault("created_at", ""),
                            row.getOrDefault("updated_at", "")
                    );

                    allGrades.add(grade);

                    // Group by semester first, then by class
                    String semesterKey = grade.getSemesterName();
                    String classKey = grade.getSubjectName();
                    
                    if (!semesters.contains(semesterKey)) {
                        semesters.add(semesterKey);
                    }

                    gradesBySemesterAndClass.computeIfAbsent(semesterKey, k -> new HashMap<>())
                            .computeIfAbsent(classKey, k -> new ArrayList<>())
                            .add(grade);
                }

                // Set adapter
                gradesAdapter = new GradesAdapter(semesters, gradesBySemesterAndClass);
                gradesRecyclerView.setAdapter(gradesAdapter);
            } else {
                Toast.makeText(this, "No grades found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading grades: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(StudentGradesActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_posts) {
                    Intent intent = new Intent(StudentGradesActivity.this, PostsActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_study) {
                    //Study button - stay in StudentGradesActivity
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_account) {
                    Intent intent = new Intent(StudentGradesActivity.this, AccountMenuActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (itemId == R.id.menu_logout) {
                    SharedPrefManager.getInstance(StudentGradesActivity.this).logout();
                    Intent intent = new Intent(StudentGradesActivity.this, LoginActivity.class);
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
