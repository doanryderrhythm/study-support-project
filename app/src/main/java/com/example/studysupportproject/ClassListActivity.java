package com.example.studysupportproject;

import static android.view.View.GONE;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ClassListActivity extends AppCompatActivity implements ClassListAdapter.OnClassActionListener {
    private RecyclerView classRecyclerView;
    private ClassListAdapter classListAdapter;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private FloatingActionButton fabAddClass;
    private ConSQL conSQL;
    private String semesterName;
    private int semesterId;

    private int currentUserId;
    private ImageView ivProfilePicture;
    private TextView tvProfileName;
    private int schoolId;
    private DatabaseHelper dbHelper;
    private boolean isTeacherMode;
    private int teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        semesterId = getIntent().getIntExtra("semester_id", -1);
        semesterName = getIntent().getStringExtra("semester_name");
        schoolId = getIntent().getIntExtra("school_id", -1);
        isTeacherMode = getIntent().getBooleanExtra("is_teacher_mode", false);



        dbHelper = new DatabaseHelper();

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
        if (isTeacherMode) {
            teacherId = SharedPrefManager.getInstance(this).getUser().getId();
            menuButton.setVisibility(GONE);
        }

        // Setup RecyclerView
        classRecyclerView = findViewById(R.id.class_recycler_view);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup FAB - only visible if entered from SemesterListActivity (which passes school_id) and not in teacher mode
        fabAddClass = findViewById(R.id.fab_add_class);
        if (schoolId != -1 && !isTeacherMode) {
            fabAddClass.setVisibility(View.VISIBLE);
            fabAddClass.setOnClickListener(v -> {
                Intent intent = new Intent(ClassListActivity.this, ClassDetailActivity.class);
                intent.putExtra("class_id", -1); // new class
                intent.putExtra("semester_id", semesterId);
                intent.putExtra("school_id", schoolId);
                startActivityForResult(intent, 100);
            });
        } else {
            fabAddClass.setVisibility(GONE);
        }

        conSQL = new ConSQL();

        setupMenuButton();
        setupNavigationViewMenu();
        loadClassesForSemester();
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
                List<ClassItem> classes;
                
                if (isTeacherMode) {
                    // Load only classes assigned to this teacher
                    classes = dbHelper.getTeacherClassesBySemester(semesterId, teacherId);
                } else {
                    // Load all classes in the semester
                    classes = dbHelper.getClassesBySemester(semesterId);
                }
                
                runOnUiThread(() -> {
                    if (classes.isEmpty()) {
                        Toast.makeText(ClassListActivity.this, "No classes found", Toast.LENGTH_SHORT).show();
                    } else {
                        classListAdapter = new ClassListAdapter(ClassListActivity.this);
                        classListAdapter.setClasses(classes);
                        classListAdapter.setOnClassClickListener(classItem -> {
                            Intent intent = new Intent(ClassListActivity.this, StudentGradesEditActivity.class);
                            intent.putExtra("semester_name", semesterName);
                            intent.putExtra("class_name", classItem.getClassName());
                            intent.putExtra("class_id", classItem.getId());
                            intent.putExtra("is_teacher_mode", isTeacherMode);
                            startActivity(intent);
                        });
                        classListAdapter.setOnClassActionListener(ClassListActivity.this);
                        classRecyclerView.setAdapter(classListAdapter);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(ClassListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public void onEditClass(ClassItem classItem) {
        if (isTeacherMode) {
            Toast.makeText(this, "Teachers cannot edit classes", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(ClassListActivity.this, ClassDetailActivity.class);
        intent.putExtra("class_id", classItem.getId());
        intent.putExtra("semester_id", semesterId);
        intent.putExtra("school_id", schoolId);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onDeleteClass(ClassItem classItem) {
        if (isTeacherMode) {
            Toast.makeText(this, "Teachers cannot delete classes", Toast.LENGTH_SHORT).show();
            return;
        }
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            dbHelper.deleteClass(classItem.getId());
                            runOnUiThread(() -> {
                                Toast.makeText(ClassListActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                                loadClassesForSemester();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(ClassListActivity.this, "Error deleting class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadClassesForSemester();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
