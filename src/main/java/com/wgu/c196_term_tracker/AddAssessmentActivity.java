package com.wgu.c196_term_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

import database.AppDatabase;
import entities.Assessment;
import entities.AssessmentType;

public class AddAssessmentActivity extends AppCompatActivity {
    EditText assessmentNameEdit;
    Button assessmentTypeSelectBTN;
    private Button assessmentStartDateSelectBTN;
    private LocalDate assessmentStartDate;
    Button assessmentEndSelectBTN;
    Button saveButton;
    Calendar calendar;
    DatePickerDialog picker;
    LocalDate assessmentEndDate;
    AssessmentType assessmentType;
    AppDatabase database;
    int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("COURSE_ID")) {
            courseId = intent.getIntExtra("COURSE_ID", -1);
        }
        populateView();
    }

    // Method for back button
    @Override
    public boolean onSupportNavigateUp() {
        // Finish the current activity and return to the previous one
        finish();
        return true;
    }

    // Sets up view
    private void populateView() {
        assessmentNameEdit = findViewById(R.id.assessmentNameEditText);
        assessmentTypeSelectBTN = findViewById(R.id.assessmentTypeSelectBTN);
        assessmentStartDateSelectBTN = findViewById(R.id.assessmentStartSelectBTN);
        assessmentEndSelectBTN = findViewById(R.id.assessmentEndSelectBTN);
        saveButton = findViewById(R.id.saveBTN);

        setupTypePicker();
        startDatePicker();
        setupEndDatePicker();
        addAssessment();
    }

    // Sets up Type picker
    private void setupTypePicker() {
        assessmentTypeSelectBTN.setOnClickListener(v -> {
            String[] types = {"Performance", "Objective"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddAssessmentActivity.this);
            builder.setTitle("Select Assessment Type")
                    .setItems(types, (dialog, which) -> {
                        assessmentTypeSelectBTN.setText(types[which]);
                        assessmentType = AssessmentType.valueOf(types[which]);
                    });
            builder.create().show();
        });
    }

    private void startDatePicker() {
        assessmentStartDateSelectBTN.setOnClickListener(v -> {
            // Set values for the calendar
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(AddAssessmentActivity.this, (view, year1, month1, dayOfMonth) -> {
                assessmentStartDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                assessmentStartDateSelectBTN.setText(assessmentStartDate.toString());
            }, year, month, day);
            picker.show();
        });
    }

    // sets up date picker
    private void setupEndDatePicker() {
        assessmentEndSelectBTN.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            picker = new DatePickerDialog(AddAssessmentActivity.this, (view, year1, month1, dayOfMonth) -> {
                assessmentEndDate = LocalDate.of(year1, month1 + 1, dayOfMonth);
                assessmentEndSelectBTN.setText(assessmentEndDate.toString());
            }, year, month, day);
            picker.show();
        });
    }

    // checks for null values
    private boolean isNull() {
        if (assessmentNameEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please add an assessment name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (assessmentStartDate == null) {
            Toast.makeText(this, "You must select a start date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (assessmentEndDate == null) {
            Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
            return true;
        }else if (assessmentType == null) {
            Toast.makeText(this, "Please select an assessment type", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    // Checks if end is before start
    private boolean isError() {
        if (assessmentEndDate.isBefore(assessmentStartDate)) {
            // courseEndDate is before courseStartDate
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    // method to save
    private void addAssessment() {
        saveButton.setOnClickListener(v -> {
            if (isNull()) {
                return;
            }
            // Check for end date before start date
            if (isError()) {
                return;
            }

            String assessmentName = assessmentNameEdit.getText().toString();
            Assessment assessment = new Assessment(null, assessmentName, assessmentStartDate, assessmentType, assessmentEndDate, courseId);

            database.assessmentDAO().insert(assessment);
            Toast.makeText(AddAssessmentActivity.this, "Assessment added successfully", Toast.LENGTH_SHORT).show();
            // Navigate back to CourseDetailActivity with the course ID
            Intent intent = new Intent(AddAssessmentActivity.this, CourseDetailActivity.class);
            intent.putExtra("COURSE_ID", courseId);
            startActivity(intent);
            finish();
        });
    }


}