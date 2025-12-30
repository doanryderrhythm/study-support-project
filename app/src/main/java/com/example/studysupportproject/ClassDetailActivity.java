package com.example.studysupportproject;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ClassDetailActivity extends AppCompatActivity {

    private EditText etClassName;
    private Spinner spSubject;
    private Button btnManageStudents;
    private Button btnManageTeachers;
    private Button btnSave;
    private Button btnCancel;
    private FloatingActionButton fabDeleteClass;
    private ImageButton menuButton;

    private DatabaseHelper dbHelper;
    private int classId = -1;
    private int semesterId;
    private int schoolId;
    private List<Subject> subjects;
    private List<Subject> subjectsLoaded;
    private int selectedSubjectId = -1;
    private ArrayAdapter<Subject> subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        dbHelper = new DatabaseHelper();

        // Get data from intent
        classId = getIntent().getIntExtra("class_id", -1);
        semesterId = getIntent().getIntExtra("semester_id", -1);
        schoolId = getIntent().getIntExtra("school_id", -1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (classId == -1) {
                getSupportActionBar().setTitle("Thêm lớp học mới");
            } else {
                getSupportActionBar().setTitle("Chỉnh sửa lớp học");
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        menuButton.setVisibility(GONE);

        // Setup views
        etClassName = findViewById(R.id.et_class_name);
        spSubject = findViewById(R.id.sp_subject);
        btnManageStudents = findViewById(R.id.btn_manage_students);
        btnManageTeachers = findViewById(R.id.btn_manage_teachers);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        fabDeleteClass = findViewById(R.id.fab_delete_class);

        // Load subjects and setup spinner
        subjects = new ArrayList<>();
        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(subjectAdapter);
        loadSubjects();

        // Manage students button
        btnManageStudents.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDetailActivity.this, StudentListActivity.class);
            intent.putExtra("class_id", classId);
            startActivityForResult(intent, 100);
        });

        // Manage teachers button
        btnManageTeachers.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDetailActivity.this, TeacherListActivity.class);
            intent.putExtra("class_id", classId);
            startActivityForResult(intent, 101);
        });

        // Save button
        btnSave.setOnClickListener(v -> saveClass());

        // Cancel button
        btnCancel.setOnClickListener(v -> onBackPressed());

        // Delete button (only show if editing existing class)
        if (classId != -1) {
            fabDeleteClass.setVisibility(View.VISIBLE);
            fabDeleteClass.setOnClickListener(v -> deleteClass());
        } else {
            fabDeleteClass.setVisibility(GONE);
        }

        // Load class data if editing
        if (classId != -1) {
            loadClassData();
        }
    }

    private void loadClassData() {
        new Thread(() -> {
            ClassItem classItem = dbHelper.getClassById(classId);
            List<Subject> classSubjects = dbHelper.getSubjectsByClass(classId);
            
            if (classItem != null) {
                runOnUiThread(() -> {
                    etClassName.setText(classItem.getClassName());
                    
                    // Set the subject in spinner if class has subjects
                    if (classSubjects != null && !classSubjects.isEmpty()) {
                        Subject classSubject = classSubjects.get(0);
                        for (int i = 0; i < subjects.size(); i++) {
                            if (subjects.get(i).getId() == classSubject.getId()) {
                                spSubject.setSelection(i);
                                selectedSubjectId = classSubject.getId();
                                break;
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void loadSubjects() {
        new Thread(() -> {
            List<Subject> loadedSubjects = dbHelper.getAllSubjects();
            runOnUiThread(() -> {
                subjects.clear();
                subjects.add(new Subject(-1, "-- Select Subject --")); // Add default option
                subjects.addAll(loadedSubjects);
                subjectAdapter.notifyDataSetChanged();
                
                // Load class data after subjects are loaded
                if (classId != -1) {
                    loadClassData();
                }
            });
        }).start();
    }

    private void saveClass() {
        String className = etClassName.getText().toString().trim();
        
        if (className.isEmpty()) {
            Toast.makeText(this, "Please fill class name", Toast.LENGTH_SHORT).show();
            return;
        }

        Subject selectedSubject = (Subject) spSubject.getSelectedItem();
        int subjectId = selectedSubject != null && selectedSubject.getId() > 0 ? selectedSubject.getId() : -1;

        new Thread(() -> {
            boolean success;
            if (classId == -1) {
                // Create new class with schoolId
                long id = dbHelper.addClass(className, semesterId, schoolId);
                success = id > 0;
                // Add subject if selected
                if (success && subjectId > 0) {
                    dbHelper.updateClassSubject((int) id, subjectId);
                }
            } else {
                // Update existing class
                success = dbHelper.updateClass(classId, className);
                // Update subject if selected
                if (success) {
                    dbHelper.updateClassSubject(classId, subjectId);
                }
            }

            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(ClassDetailActivity.this, "Class saved successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ClassDetailActivity.this, "Failed to save class", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void deleteClass() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        boolean success = dbHelper.deleteClass(classId);
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(ClassDetailActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(ClassDetailActivity.this, "Failed to delete class", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Student list activity returned - no action needed
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            // Teacher list activity returned - no action needed
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
