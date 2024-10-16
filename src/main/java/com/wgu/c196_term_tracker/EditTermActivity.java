package com.wgu.c196_term_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

import database.AppDatabase;
import entities.Term;

public class EditTermActivity extends AppCompatActivity {
    EditText termNameEdit;
    Button termStartDateSelect;
    Button termEndDateSelect;
    Button saveButton;
    Term term;
    LocalDate termStartDate;
    LocalDate termEndDate;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    Calendar calender;
    DatePickerDialog picker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        AppDatabase database = AppDatabase.getInstance(this.getApplicationContext());
        populateView(database);
    }

    private void populateView(AppDatabase database) {
        termNameEdit = findViewById(R.id.editTermName);
        termStartDateSelect = findViewById(R.id.editTermStartBTN);
        termEndDateSelect = findViewById(R.id.editTermEndBTN);
        saveButton = findViewById(R.id.saveBTN);

        startDatePicker();
        endDatePicker();
        loadTermData(database);
        saveTerm(database);
    }

    // Method for back button
    @Override
    public boolean onSupportNavigateUp() {
        // Finish the current activity and return to the previous one
        finish();
        return true;
    }

    // method to receive the term ID from the intent extra and sets the data in the fields
    private void loadTermData(AppDatabase database) {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("TERM_ID")) {
            int termId = intent.getIntExtra("TERM_ID", -1);
            if (termId != -1) {
                term = database.termDAO().getTermById(termId);
                if (term != null) {
                    termNameEdit.setText(term.getTermName());
                    termStartDateSelect.setText(term.getStartDate().format(formatter));
                    termEndDateSelect.setText(term.getEndDate().format(formatter));
                    termStartDate = term.getStartDate();
                    termEndDate = term.getEndDate();
                }
            }
        }
    }

    private void saveTerm(AppDatabase database) {
        saveButton.setOnClickListener(v -> {
            String termName = termNameEdit.getText().toString();
            // Check for null values
            if (isNull()) {
                return;
            }
            // Check for end date before start date
            if (isError()) {
                return;
            }
            // Update the term in the database
            term.setTermName(termName);
            term.setStartDate(termStartDate);
            term.setEndDate(termEndDate);
            database.termDAO().update(term);
            Toast.makeText(EditTermActivity.this, "Term was updated successfully", Toast.LENGTH_SHORT).show();
            // Send the user back to the MainTermActivity
            Intent intent = new Intent(EditTermActivity.this, MainTermActivity.class);
            startActivity(intent);
        });
    }

    // Errors for null values
    private boolean isNull() {
        if (termNameEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter a term name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (termStartDate == null) {
            Toast.makeText(this, "You must select a start date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (termEndDate == null) {
            Toast.makeText(this, "You must select an end date", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    private boolean isError() {
        if (termEndDate.isBefore(termStartDate)) {
            // termEndDate is before termStartDate
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    // Creates a date picker
    private void startDatePicker() {
        termStartDateSelect.setOnClickListener(v -> {
            // Set values for the calendar
            calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(EditTermActivity.this, (view, year1, month1, dayOfMonth) -> {
                termStartDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                termStartDateSelect.setText(formatter.format(termStartDate));
            }, year, month, day);
            picker.show();
        });
    }

    // Creates a date picker
    private void endDatePicker() {
        termEndDateSelect.setOnClickListener(v -> {
            // Set values for the calendar
            calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(EditTermActivity.this, (view, year1, month1, dayOfMonth) -> {
                termEndDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                termEndDateSelect.setText(formatter.format(termEndDate));
            }, year, month, day);
            picker.show();
        });
    }

}