package com.example.studysupportproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleDetailActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etDescription;
    private EditText etDate;
    private EditText etStartTime;
    private EditText etEndTime;
    private Spinner spType;
    private Button btnSave;
    private Button btnCancel;

    private DatabaseHelper dbHelper;
    private int scheduleId = -1;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper();
        initializeViews();
        getIntentData();
        setupListeners();
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.et_schedule_title);
        etDescription = findViewById(R.id.et_schedule_description);
        etDate = findViewById(R.id.et_schedule_date);
        etStartTime = findViewById(R.id.et_schedule_start_time);
        etEndTime = findViewById(R.id.et_schedule_end_time);
        spType = findViewById(R.id.sp_schedule_type);
        btnSave = findViewById(R.id.btn_schedule_save);
        btnCancel = findViewById(R.id.btn_schedule_cancel);

        // Set input types for proper masking
        etDate.setInputType(android.text.InputType.TYPE_NULL);
        etStartTime.setInputType(android.text.InputType.TYPE_NULL);
        etEndTime.setInputType(android.text.InputType.TYPE_NULL);

        // Make EditTexts read-only (click triggers picker)
        etDate.setFocusable(false);
        etStartTime.setFocusable(false);
        etEndTime.setFocusable(false);

        // Set click listeners for date/time pickers
        etDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showStartTimePicker());
        etEndTime.setOnClickListener(v -> showEndTimePicker());
    }

    private void getIntentData() {
        Intent intent = getIntent();
        scheduleId = intent.getIntExtra("schedule_id", -1);
        userId = intent.getIntExtra("user_id", -1);

        if (scheduleId != -1) {
            // Edit mode - load existing schedule
            String title = intent.getStringExtra("schedule_title");
            String description = intent.getStringExtra("schedule_description");
            String date = intent.getStringExtra("schedule_date");
            String startTime = intent.getStringExtra("schedule_start_time");
            String endTime = intent.getStringExtra("schedule_end_time");
            String type = intent.getStringExtra("schedule_type");

            etTitle.setText(title != null ? title : "");
            etDescription.setText(description != null ? description : "");
            etDate.setText(date != null ? date : "");
            etStartTime.setText(startTime != null ? formatTimeToMinutes(startTime) : "");
            etEndTime.setText(endTime != null ? formatTimeToMinutes(endTime) : "");

            // Set spinner value
            if (type != null) {
                int position = getSpinnerPosition(type);
                spType.setSelection(position);
            }

            getSupportActionBar().setTitle("Edit Schedule");
        } else {
            getSupportActionBar().setTitle("Add New Schedule");
        }
    }

    private String formatTimeToMinutes(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return "";
        }
        
        try {
            // Handle HH:mm format (already correct)
            if (timeStr.length() <= 5) {
                return timeStr;
            }
            
            // If it has seconds (HH:mm:ss), truncate to HH:mm
            return timeStr.substring(0, 5);
        } catch (Exception e) {
            return timeStr;
        }
    }

    private int getSpinnerPosition(String type) {
        String[] types = {"personal", "study", "meeting", "work", "teaching", "group_work", "exam"};
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type)) {
                return i;
            }
        }
        return 0;
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveSchedule());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveSchedule() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String type = spType.getSelectedItem().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter schedule title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Please enter schedule date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save in background thread
        new Thread(() -> {
            try {
                if (scheduleId != -1) {
                    // Update existing schedule
                    boolean success = dbHelper.updateSchedule(scheduleId, title, description,
                            date, startTime, endTime);
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(ScheduleDetailActivity.this, "Schedule updated successfully",
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ScheduleDetailActivity.this, "Failed to update schedule",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Insert new schedule
                    long newId = dbHelper.addSchedule(userId, title, description,
                            date, startTime, endTime, type);
                    runOnUiThread(() -> {
                        if (newId > 0) {
                            Toast.makeText(ScheduleDetailActivity.this, "Schedule created successfully",
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ScheduleDetailActivity.this, "Failed to create schedule",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ScheduleDetailActivity.this,
                            "Error saving schedule: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        
        // Try to parse existing date if available
        if (!etDate.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.setTime(sdf.parse(etDate.getText().toString()));
            } catch (Exception e) {
                // Use current date
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        Calendar calendar = Calendar.getInstance();
        
        // Try to parse existing time if available
        if (!etStartTime.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                calendar.setTime(sdf.parse(etStartTime.getText().toString()));
            } catch (Exception e) {
                // Use current time
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etStartTime.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    private void showEndTimePicker() {
        Calendar calendar = Calendar.getInstance();
        
        // Try to parse existing time if available
        if (!etEndTime.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                calendar.setTime(sdf.parse(etEndTime.getText().toString()));
            } catch (Exception e) {
                // Use current time
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etEndTime.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }
}
