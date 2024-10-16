package com.wgu.c196_term_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

import database.AppDatabase;
import entities.Term;

public class AddTermActivity extends AppCompatActivity {
    Term createTerm;
    AppDatabase db;
    Button termStartSelectBTN;
    Button termEndSelectBTN;
    Button addTermBtn;
    String termName;
    EditText termNameEditText;
    Calendar calender;
    DatePickerDialog picker;
    LocalDate termStartDate;
    LocalDate termEndDate;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
        // Back Button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //Get an instance of the database.
        db = AppDatabase.getInstance(this.getApplicationContext());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        populateView();
    }

    private void populateView() {
        termNameEditText = findViewById(R.id.termNameEditText);
        startDatePicker();
        endDatePicker();
        addTerm();
    }

    private void addTerm() {
        addTermBtn = findViewById(R.id.addTermBTN);
        // Set an on-click listener
        addTermBtn.setOnClickListener(v -> {
            termName = termNameEditText.getText().toString();
            // checks for null values
            if (isNull()) {
                return;
            }
            // checks for end date before start date
            if (isError()) {
                return;
            }
            // If all fields are filled in, add the new term to the database.
            createTerm = new Term(null, termName, termStartDate, termEndDate );
            db.termDAO().insertAll(createTerm);
            Toast.makeText(AddTermActivity.this, "Term was added successfully", Toast.LENGTH_SHORT).show();
            // Send the user back to the MainTermActivity.
            Intent intent = new Intent(AddTermActivity.this, MainTermActivity.class);
            startActivity(intent);
        });
    }

    // Errors for null values
    private boolean isNull() {
        if (termNameEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter a term name", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (termStartDate == null){
            Toast.makeText(this, "You must select a start date ", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (termEndDate == null){
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

    // Method for back button
    @Override
    public boolean onSupportNavigateUp() {
        // Finish the current activity and return to the previous one
        finish();
        return true;
    }

    // Creates a date picker
    private void startDatePicker() {
        termStartSelectBTN = findViewById(R.id.termStartSelectBTN);
        //Set an on-click listener
        termStartSelectBTN.setOnClickListener(v -> {
            //Set values for the calendar
            calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);
            //Create a date picker dialog
            picker = new DatePickerDialog(AddTermActivity.this, (view, year1, month1, dayOfMonth) -> {
                termStartDate = LocalDate.of(year1, (month1 +1), dayOfMonth);
                termStartSelectBTN.setText(formatter.format(termStartDate));
            }, year, month, day);
            picker.show();
        });
    }

    // Creates a date picker
    private void endDatePicker() {
        termEndSelectBTN = findViewById(R.id.termEndSelectBTN);
        // Set an on-click listener
        termEndSelectBTN.setOnClickListener(v -> {
            // Set values for the calendar
            calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(AddTermActivity.this, (view, year1, month1, dayOfMonth) -> {
                termEndDate = LocalDate.of(year1, (month1 +1), dayOfMonth);
                termEndSelectBTN.setText(formatter.format(termEndDate));
            }, year, month, day);
            picker.show();
        });
    }


}