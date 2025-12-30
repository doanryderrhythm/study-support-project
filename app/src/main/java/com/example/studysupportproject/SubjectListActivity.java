package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SubjectListActivity extends AppCompatActivity {

    private RecyclerView rvSubjects;
    private FloatingActionButton fabAddSubject;
    private SubjectAdapter subjectAdapter;
    private ImageButton menuButton;
    private DatabaseHelper dbHelper;
    private int schoolId;
    private String schoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        dbHelper = new DatabaseHelper();

        // Get data from intent
        schoolId = getIntent().getIntExtra("school_id", -1);
        schoolName = getIntent().getStringExtra("school_name");

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Subjects - " + schoolName);
        }
        menuButton = findViewById(R.id.menu_button);
        menuButton.setVisibility(View.GONE);

        // Setup views
        rvSubjects = findViewById(R.id.rv_subjects);
        fabAddSubject = findViewById(R.id.fab_add_subject);

        // Setup RecyclerView
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        subjectAdapter = new SubjectAdapter(this);

        // Set edit listener for subjects
        subjectAdapter.setOnSubjectEditListener(subject -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Edit Subject");

            android.widget.EditText input = new android.widget.EditText(this);
            input.setText(subject.getName());
            input.setSelection(input.getText().length());
            input.setPadding(16, 16, 16, 16);
            builder.setView(input);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String subjectName = input.getText().toString().trim();
                if (subjectName.isEmpty()) {
                    Toast.makeText(SubjectListActivity.this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        subject.setName(subjectName);
                        dbHelper.updateSubject(subject);
                        runOnUiThread(() -> {
                            Toast.makeText(SubjectListActivity.this, "Subject updated", Toast.LENGTH_SHORT).show();
                            loadSubjects();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(SubjectListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        // Set delete listener for subjects
        subjectAdapter.setOnSubjectDeleteListener(subject -> {
            new Thread(() -> {
                try {
                    boolean hasClasses = dbHelper.hasClassesAttached(subject.getId());
                    runOnUiThread(() -> {
                        if (hasClasses) {
                            Toast.makeText(SubjectListActivity.this, "Cannot delete subject - classes are still attached", Toast.LENGTH_SHORT).show();
                        } else {
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Delete Subject")
                                    .setMessage("Are you sure you want to delete this subject?")
                                    .setPositiveButton("Delete", (dialog, which) -> {
                                        new Thread(() -> {
                                            try {
                                                dbHelper.deleteSubject(subject.getId());
                                                runOnUiThread(() -> {
                                                    Toast.makeText(SubjectListActivity.this, "Subject deleted", Toast.LENGTH_SHORT).show();
                                                    loadSubjects();
                                                });
                                            } catch (Exception e) {
                                                runOnUiThread(() -> Toast.makeText(SubjectListActivity.this, "Error deleting subject: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                            }
                                        }).start();
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(SubjectListActivity.this, "Error checking subject usage: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        rvSubjects.setAdapter(subjectAdapter);

        // Setup FAB
        fabAddSubject.setOnClickListener(v -> {
            // Show dialog to add new subject
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Add Subject");

            android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("Enter subject name");
            input.setPadding(16, 16, 16, 16);
            builder.setView(input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String subjectName = input.getText().toString().trim();
                if (subjectName.isEmpty()) {
                    Toast.makeText(SubjectListActivity.this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        boolean success = dbHelper.addSubject(subjectName, schoolId);
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(SubjectListActivity.this, "Subject added", Toast.LENGTH_SHORT).show();
                                loadSubjects();
                            } else {
                                Toast.makeText(SubjectListActivity.this, "Failed to add subject", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(SubjectListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        loadSubjects();
    }

    private void loadSubjects() {
        new Thread(() -> {
            List<Subject> subjects = dbHelper.getAllSubjects();
            runOnUiThread(() -> {
                if (subjects != null && !subjects.isEmpty()) {
                    subjectAdapter.setSubjects(subjects);
                    rvSubjects.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SubjectListActivity.this, "No subjects found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
