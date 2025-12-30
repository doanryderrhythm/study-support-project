package com.example.studysupportproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SchoolDetailActivity extends AppCompatActivity {

    private EditText etSchoolName;
    private Button btnSave;
    private Button btnCancel;

    private DatabaseHelper dbHelper;
    private int schoolId = -1;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_detail);

        dbHelper = new DatabaseHelper();

        // Get data from intent if editing
        schoolId = getIntent().getIntExtra("school_id", -1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (schoolId == -1) {
                getSupportActionBar().setTitle("Thêm trường học mới");
            } else {
                getSupportActionBar().setTitle("Chỉnh sửa trường học");
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(ImageButton.GONE);
        // Setup views
        etSchoolName = findViewById(R.id.et_school_name);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Save button
        btnSave.setOnClickListener(v -> saveSchool());

        // Cancel button
        btnCancel.setOnClickListener(v -> onBackPressed());

        // Load school data if editing
        if (schoolId != -1) {
            loadSchoolData();
        }
    }

    private void loadSchoolData() {
        new Thread(() -> {
            School school = dbHelper.getSchoolById(schoolId);
            if (school != null) {
                runOnUiThread(() -> {
                    etSchoolName.setText(school.getSchoolName());
                });
            }
        }).start();
    }

    private void saveSchool() {
        String schoolName = etSchoolName.getText().toString().trim();

        if (schoolName.isEmpty()) {
            Toast.makeText(this, "Please enter school name", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            boolean success;
            if (schoolId == -1) {
                // Create new school
                long id = dbHelper.addSchool(schoolName);
                success = id > 0;
            } else {
                // Update existing school
                success = dbHelper.updateSchool(schoolId, schoolName);
            }

            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(SchoolDetailActivity.this, "School saved successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(SchoolDetailActivity.this, "Failed to save school", Toast.LENGTH_SHORT).show();
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
