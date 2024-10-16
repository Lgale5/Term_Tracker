package com.wgu.c196_term_tracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

import database.AppDatabase;
import entities.Assessment;
import entities.AssessmentType;

public class EditAssessmentActivity extends AppCompatActivity {
    EditText assessmentNameEdit;
    Button assessmentEndEditBTN;
    Button typeSelectEditBTN;
    Button saveButton;
    private Button assessmentStartEditBTN;
    private LocalDate assessmentStartDate;
    Assessment assessment;
    LocalDate assessmentEndDate;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    Calendar calendar;
    DatePickerDialog picker;
    AppDatabase database;
    int typeIndex;
    AssessmentType assessmentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        populateView();
    }

    private void populateView() {

        assessmentNameEdit = findViewById(R.id.editAssessmentNameText);
        assessmentStartEditBTN = findViewById(R.id.assessmentStartEditBTN);
        assessmentEndEditBTN = findViewById(R.id.assessmentEndEditBTN);
        typeSelectEditBTN = findViewById(R.id.typeSelectEditBTN);
        saveButton = findViewById(R.id.saveBTN);

        startDatePicker();
        endDatePicker();
        setupTypePicker();
        loadAssessmentData();
        saveAssessment();
    }

    // Method for back button
    @Override
    public boolean onSupportNavigateUp() {
        // Finish the current activity and return to the previous one
        finish();
        return true;
    }

    private void loadAssessmentData() {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("ASSESSMENT_ID")) {
                int assessmentId = intent.getIntExtra("ASSESSMENT_ID", -1);
                if (assessmentId != -1) {
                    assessment = database.assessmentDAO().getAssessmentById(assessmentId);
                    if (assessment != null) {
                        assessmentStartEditBTN.setText(assessment.getStartDate().toString());
                        assessmentStartDate = assessment.getStartDate();
                        assessmentNameEdit.setText(assessment.getAssessmentName());
                        assessmentEndEditBTN.setText(assessment.getEndDate().toString());
                        assessmentEndDate = assessment.getEndDate();

                        // Load type into form
                        if (assessment.getAssessmentType() != null) {
                            AssessmentType type = assessment.getAssessmentType();
                            typeSelectEditBTN.setText(type.name());
                            assessmentType = type;
                        }
                    }
                }
            }
        }

    private void startDatePicker() {
        assessmentStartEditBTN.setOnClickListener(v -> {
            // Set values for the calendar
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(EditAssessmentActivity.this, (view, year1, month1, dayOfMonth) -> {
                assessmentStartDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                assessmentStartEditBTN.setText(assessmentStartDate.toString());
            }, year, month, day);
            picker.show();
        });
    }

    // Creates end date picker
    private void endDatePicker() {
        assessmentEndEditBTN.setOnClickListener(v -> {
            // Set values for the calendar
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(EditAssessmentActivity.this, (view, year1, month1, dayOfMonth) -> {
                assessmentEndDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                assessmentEndEditBTN.setText(assessmentEndDate.toString());
            }, year, month, day);
            picker.show();
        });
    }

    // Sets up Type picker
    private void setupTypePicker() {
        typeSelectEditBTN.setOnClickListener(v -> {
            String[] types = {"Performance", "Objective"};
            AlertDialog.Builder builder = new AlertDialog.Builder(EditAssessmentActivity.this);
            builder.setTitle("Select Assessment Type")
                    .setItems(types, (dialog, which) -> {
                        typeSelectEditBTN.setText(types[which]);
                        assessmentType = AssessmentType.valueOf(types[which]);
                    });
            builder.create().show();
        });
    }

    // Errors for null values
    private boolean isNull() {
        if (assessmentNameEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter an assessment name", Toast.LENGTH_SHORT).show();
            return true;
        }if (assessmentStartDate == null) {
            Toast.makeText(this, "You must select a start date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (assessmentEndDate == null) {
            Toast.makeText(this, "You must select an end date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (assessmentType == null) {
            Toast.makeText(this, "You must select a type", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    // Checks if end is before start
    private boolean isError() {
        if (assessmentEndDate.isBefore(assessmentStartDate)) {
            // courseEndDate is before courseStartDate
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    private void saveAssessment() {
        saveButton.setOnClickListener(v -> {
            String assessmentName = assessmentNameEdit.getText().toString();
            // Check for null values
            if (isNull()) {
                return;
            }
            // Check for end date before start date
            if (isError()) {
                return;
            }

            // Update the assessment in the database
            assessment.setAssessmentName(assessmentName);
            assessment.setStartDate(assessmentStartDate);
            assessment.setEndDate(assessmentEndDate);
            assessment.setAssessmentType(assessmentType);

            try {
                database.assessmentDAO().update(assessment);
                Toast.makeText(EditAssessmentActivity.this, "Assessment was updated successfully", Toast.LENGTH_SHORT).show();
                // Send the user back to the CourseDetailActivity
                Intent intent = new Intent(EditAssessmentActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("ASSESSMENT_ID", assessment.getAssessmentID());
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(EditAssessmentActivity.this, "Error updating assessment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

}