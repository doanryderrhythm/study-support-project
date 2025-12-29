package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class StudentGradeDetailActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navView;
    private Button submitButton;
    private ConSQL conSQL;
    
    private int studentId;
    private String studentName;
    private int classId;
    private String className;
    private String semesterName;
    
    // Grade type fields
    private EditText etQuaTrinhGrade;
    private EditText etGiuaKyGrade;
    private EditText etCuoiKyGrade;
    private EditText etThucHanhGrade;
    
    // Notes fields
    private EditText etQuaTrinhNotes;
    private EditText etGiuaKyNotes;
    private EditText etCuoiKyNotes;
    private EditText etThucHanhNotes;
    
    // Grade IDs (for updates)
    private int gradeIdQuaTrinhGrade;
    private int gradeIdGiuaKy;
    private int gradeIdCuoiKy;
    private int gradeIdThucHanh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grade_detail);

        studentId = getIntent().getIntExtra("student_id", -1);
        studentName = getIntent().getStringExtra("student_name");
        classId = getIntent().getIntExtra("class_id", -1);
        className = getIntent().getStringExtra("class_name");
        semesterName = getIntent().getStringExtra("semester_name");

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Edit Grades - " + studentName);
            }
        }

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        navView = findViewById(R.id.nav_view);
        submitButton = findViewById(R.id.submit_button);
        
        if (drawerLayout == null || menuButton == null || navView == null || submitButton == null) {
            Toast.makeText(this, "Error: Drawer views not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initializeGradeFields();
        
        conSQL = new ConSQL();

        setupMenuButton();
        setupNavigationViewMenu();
        setupSubmitButton();
        loadStudentGrades();
    }

    private void initializeGradeFields() {
        try {
            // Grade input fields
            etQuaTrinhGrade = findViewById(R.id.et_qua_trinh_grade);
            etGiuaKyGrade = findViewById(R.id.et_giua_ky_grade);
            etCuoiKyGrade = findViewById(R.id.et_cuoi_ky_grade);
            etThucHanhGrade = findViewById(R.id.et_thuc_hanh_grade);
            
            // Notes fields
            etQuaTrinhNotes = findViewById(R.id.et_qua_trinh_notes);
            etGiuaKyNotes = findViewById(R.id.et_giua_ky_notes);
            etCuoiKyNotes = findViewById(R.id.et_cuoi_ky_notes);
            etThucHanhNotes = findViewById(R.id.et_thuc_hanh_notes);
            
            // Check if any views are null
            if (etQuaTrinhGrade == null || etGiuaKyGrade == null || etCuoiKyGrade == null || 
                etThucHanhGrade == null || etQuaTrinhNotes == null || etGiuaKyNotes == null || 
                etCuoiKyNotes == null || etThucHanhNotes == null) {
                Toast.makeText(this, "Error: Some views not found in layout", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            // Initialize grade IDs to 0 (means no existing grade)
            gradeIdQuaTrinhGrade = 0;
            gradeIdGiuaKy = 0;
            gradeIdCuoiKy = 0;
            gradeIdThucHanh = 0;
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing fields: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupMenuButton() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
    }

    private void setupNavigationViewMenu() {
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(StudentGradeDetailActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_posts) {
                Intent intent = new Intent(StudentGradeDetailActivity.this, PostsActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_study) {
                Intent intent = new Intent(StudentGradeDetailActivity.this, GradeManagementActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_account) {
                Intent intent = new Intent(StudentGradeDetailActivity.this, AccountMenuActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> submitGrades());
    }

    private void loadStudentGrades() {
        new Thread(() -> {
            try {
                String query = "SELECT id, grade_value, grade_type, notes " +
                        "FROM grades " +
                        "WHERE student_id = " + studentId + " AND class_id = " + classId + " " +
                        "ORDER BY grade_type";

                java.util.List<java.util.Map<String, String>> results = conSQL.executeQuery(query);

                if (results != null && !results.isEmpty()) {
                    for (java.util.Map<String, String> row : results) {
                        int gradeId = Integer.parseInt(row.getOrDefault("id", "0"));
                        String gradeValue = row.getOrDefault("grade_value", "");
                        String gradeType = row.getOrDefault("grade_type", "");
                        String notes = row.getOrDefault("notes", "");

                        // Load grade based on type
                        if (gradeType.equalsIgnoreCase("quá trình")) {
                            gradeIdQuaTrinhGrade = gradeId;
                            runOnUiThread(() -> {
                                if (etQuaTrinhGrade != null) etQuaTrinhGrade.setText(gradeValue);
                                if (etQuaTrinhNotes != null) etQuaTrinhNotes.setText(notes);
                            });
                        } else if (gradeType.equalsIgnoreCase("giữa kỳ")) {
                            gradeIdGiuaKy = gradeId;
                            runOnUiThread(() -> {
                                if (etGiuaKyGrade != null) etGiuaKyGrade.setText(gradeValue);
                                if (etGiuaKyNotes != null) etGiuaKyNotes.setText(notes);
                            });
                        } else if (gradeType.equalsIgnoreCase("cuối kỳ")) {
                            gradeIdCuoiKy = gradeId;
                            runOnUiThread(() -> {
                                if (etCuoiKyGrade != null) etCuoiKyGrade.setText(gradeValue);
                                if (etCuoiKyNotes != null) etCuoiKyNotes.setText(notes);
                            });
                        } else if (gradeType.equalsIgnoreCase("thực hành")) {
                            gradeIdThucHanh = gradeId;
                            runOnUiThread(() -> {
                                if (etThucHanhGrade != null) etThucHanhGrade.setText(gradeValue);
                                if (etThucHanhNotes != null) etThucHanhNotes.setText(notes);
                            });
                        }
                    }
                }

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(StudentGradeDetailActivity.this, "Error loading grades: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void submitGrades() {
        new Thread(() -> {
            try {
                boolean allSuccess = true;

                // Process each grade type
                allSuccess &= saveOrUpdateGrade(gradeIdQuaTrinhGrade, "quá trình", etQuaTrinhGrade, etQuaTrinhNotes);
                allSuccess &= saveOrUpdateGrade(gradeIdGiuaKy, "giữa kỳ", etGiuaKyGrade, etGiuaKyNotes);
                allSuccess &= saveOrUpdateGrade(gradeIdCuoiKy, "cuối kỳ", etCuoiKyGrade, etCuoiKyNotes);
                allSuccess &= saveOrUpdateGrade(gradeIdThucHanh, "thực hành", etThucHanhGrade, etThucHanhNotes);

                boolean finalAllSuccess = allSuccess;
                runOnUiThread(() -> {
                    if (finalAllSuccess) {
                        Toast.makeText(StudentGradeDetailActivity.this, "Grades saved successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(StudentGradeDetailActivity.this, "Some grades failed to save", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(StudentGradeDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private boolean saveOrUpdateGrade(int gradeId, String gradeType, EditText etGrade, EditText etNotes) {
        if (etGrade == null || etNotes == null) {
            return true; // Skip if views don't exist
        }

        String gradeValueStr = etGrade.getText().toString().trim();
        String notesStr = etNotes.getText().toString().trim();

        // If no grade value entered, skip
        if (gradeValueStr.isEmpty()) {
            return true;
        }

        try {
            double gradeValue = Double.parseDouble(gradeValueStr);

            if (gradeId > 0) {
                // Update existing grade
                String updateQuery = "UPDATE grades SET grade_value = " + gradeValue + 
                        ", notes = N'" + notesStr.replace("'", "''") + "' " +
                        "WHERE id = " + gradeId;
                return conSQL.executeUpdate(updateQuery);
            } else {
                // Insert new grade
                String insertQuery = "INSERT INTO grades (student_id, class_id, grade_value, grade_type, notes) " +
                        "VALUES (" + studentId + ", " + classId + ", " + gradeValue + 
                        ", N'" + gradeType + "', N'" + notesStr.replace("'", "''") + "')";
                return conSQL.executeUpdate(insertQuery);
            }
        } catch (NumberFormatException e) {
            runOnUiThread(() -> Toast.makeText(this, "Invalid grade value for " + gradeType, Toast.LENGTH_SHORT).show());
            return false;
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Error saving grade: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
