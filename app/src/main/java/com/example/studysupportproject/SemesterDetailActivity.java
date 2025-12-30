package com.example.studysupportproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SemesterDetailActivity extends AppCompatActivity {

    private EditText etSemesterName;
    private Button btnSave;
    private Button btnCancel;

    private DatabaseHelper dbHelper;
    private int semesterId = -1;
    private int schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester_detail);

        dbHelper = new DatabaseHelper();

        // Get data from intent
        schoolId = getIntent().getIntExtra("school_id", -1);
        semesterId = getIntent().getIntExtra("semester_id", -1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (semesterId == -1) {
                getSupportActionBar().setTitle("Thêm học kỳ mới");
            } else {
                getSupportActionBar().setTitle("Chỉnh sửa học kỳ");
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup views
        etSemesterName = findViewById(R.id.et_semester_name);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Save button
        btnSave.setOnClickListener(v -> saveSemester());

        // Cancel button
        btnCancel.setOnClickListener(v -> onBackPressed());

        // Load semester data if editing
        if (semesterId != -1) {
            loadSemesterData();
        }
    }

    private void loadSemesterData() {
        new Thread(() -> {
            Semester semester = dbHelper.getSemesterById(semesterId);
            if (semester != null) {
                runOnUiThread(() -> {
                    etSemesterName.setText(semester.getName());
                });
            }
        }).start();
    }

    private void saveSemester() {
        String semesterName = etSemesterName.getText().toString().trim();

        if (semesterName.isEmpty()) {
            Toast.makeText(this, "Please enter semester name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (schoolId == -1) {
            Toast.makeText(this, "Invalid school ID", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            boolean success;
            if (semesterId == -1) {
                // Create new semester
                long id = dbHelper.addSemester(schoolId, semesterName);
                success = id > 0;
            } else {
                // Update existing semester
                success = dbHelper.updateSemester(semesterId, semesterName);
            }

            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(SemesterDetailActivity.this, "Semester saved successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(SemesterDetailActivity.this, "Failed to save semester", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
