package com.wgu.c196_term_tracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import database.AppDatabase;
import entities.Course;
import entities.CourseStatus;
import entities.Instructor;
import entities.Term;

public class AddCoursesActivity extends AppCompatActivity {
    Course createCourse;
    EditText courseNameEdit;
    EditText courseNoteEdit;
    String courseName;
    Button courseStartSelectBTN;
    Button courseEndSelectBTN;
    Calendar calender;
    DatePickerDialog picker;
    LocalDate courseStartDate;
    LocalDate courseEndDate;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    Button termSelector;
    Button statusSelector;
    Button instructorSelector;
    Button addCourseButton;
    AppDatabase database;
    List<Term> termList;
    List<Instructor> instructorList;
    String[] termNames;
    String[] instructorNames;
    int termIndex = -1;
    int instructorIndex = -1;
    CourseStatus courseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        termList = database.termDAO().getAllTerms();
        instructorList = database.instructorDAO().getAllInstructors();
        populateView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, TermCoursesActivity.class);
        // Pass the term ID back to TermCoursesActivity
        intent.putExtra("TERM_ID", getIntent().getIntExtra("TERM_ID", -1));
        startActivity(intent);
        return true;
    }

    private void populateView() {
        courseNameEdit = findViewById(R.id.courseNameEditText);
        courseNoteEdit = findViewById(R.id.courseNoteEditText);
        startDatePicker();
        endDatePicker();
        populateTermSpinner();
        populateStatusSpinner();
        populateInstructorSpinner();
        addCourse();
    }

    private void startDatePicker() {
        courseStartSelectBTN = findViewById(R.id.courseStartSelectBTN);
        //Set an on-click listener
        courseStartSelectBTN.setOnClickListener(v -> {
            //Set values for the calendar
            calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);
            //Create a date picker dialog
            picker = new DatePickerDialog(AddCoursesActivity.this, (view, year1, month1, dayOfMonth) -> {
                courseStartDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                courseStartSelectBTN.setText(formatter.format(courseStartDate));
            }, year, month, day);
            picker.show();
        });
    }

    private void endDatePicker() {
        courseEndSelectBTN = findViewById(R.id.courseEndSelectBTN);
        //Set an on-click listener
        courseEndSelectBTN.setOnClickListener(v -> {
            //Set values for the calendar
            calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);
            //Create a date picker dialog
            picker = new DatePickerDialog(AddCoursesActivity.this, (view, year1, month1, dayOfMonth) -> {
                courseEndDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                courseEndSelectBTN.setText(formatter.format(courseEndDate));
            }, year, month, day);
            picker.show();
        });
    }

    private void populateTermSpinner() {
        termSelector = findViewById(R.id.termSelectBTN);
        //Populate spinner by database generated string array
        termNames = database.termDAO().getTermsArray();
        // Initialize the selected index
        AtomicInteger selected = new AtomicInteger(-1);

        // Create the AlertDialog
        AlertDialog termDialog = new AlertDialog.Builder(AddCoursesActivity.this)
                .setSingleChoiceItems(termNames, selected.get(),
                        (dialog, which) -> {
                            // Handle term selection
                            termSelector.setText(termNames[which]);
                            selected.set(which);
                            termIndex = which; // Update termIndex
                            // Dismiss the dialog
                            dialog.dismiss();
                        })
                .setTitle("Select Term")
                .create();

        // Set an OnClickListener for the termSelector
        termSelector.setOnClickListener(v -> {
            termDialog.getListView().setSelection(selected.get());
            termDialog.show();
        });
    }

    private void populateStatusSpinner() {
        statusSelector = findViewById(R.id.statusSelectBTN);
        String[] statuses = {"In Progress", "Completed", "Dropped", "Plan to Take"};
        // Initialize the selected index
        AtomicInteger selected = new AtomicInteger(-1);

        // Create the AlertDialog
        AlertDialog statusDialog = new AlertDialog.Builder(AddCoursesActivity.this)
                .setSingleChoiceItems(statuses, selected.get(),
                        (dialog, which) -> {
                            // Handle status selection
                            statusSelector.setText(statuses[which]);
                            selected.set(which);
                            switch (which) {
                                case 0:
                                    courseStatus = CourseStatus.InProgress;
                                    break;
                                case 1:
                                    courseStatus = CourseStatus.Completed;
                                    break;
                                case 2:
                                    courseStatus = CourseStatus.Dropped;
                                    break;
                                case 3:
                                    courseStatus = CourseStatus.PlanToTake;
                                    break;
                            }
                            dialog.dismiss();
                        })
                .setTitle("Select Status")
                .create();

        // Set an OnClickListener for the statusSelector
        statusSelector.setOnClickListener(v -> {
            statusDialog.getListView().setSelection(selected.get());
            statusDialog.show();
        });
    }

    private void populateInstructorSpinner() {
        instructorSelector = findViewById(R.id.instructorSelectBTN);
        //Populate spinner from database
        instructorNames = new String[instructorList.size()];
        for (int i = 0; i < instructorList.size(); i++) {
            instructorNames[i] = instructorList.get(i).getName();
        }
        // Initialize the selected index
        AtomicInteger selected = new AtomicInteger(-1);

        // Create the Alert
        AlertDialog instructorDialog = new AlertDialog.Builder(AddCoursesActivity.this)
                .setSingleChoiceItems(instructorNames, selected.get(),
                        (dialog, which) -> {
                            // Handle instructor selection
                            instructorSelector.setText(instructorNames[which]);
                            selected.set(which);
                            instructorIndex = which;
                            dialog.dismiss();
                        })
                .setTitle("Select Instructor")
                .create();

        // Set an OnClickListener for the instructorSelector
        instructorSelector.setOnClickListener(v -> {
            instructorDialog.getListView().setSelection(selected.get());
            instructorDialog.show();
        });
    }

    private boolean isNull() {
        if (courseNameEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please add a course name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (courseStartDate == null) {
            Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (courseEndDate == null) {
            Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (termIndex < 0) {
            Toast.makeText(this, "Please select a term", Toast.LENGTH_SHORT).show();
            return true;
        } else if (courseStatus == null) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
            return true;
        } else if (instructorIndex < 0) {
            Toast.makeText(this, "Please select an instructor", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    // Checks if end is before start
    private boolean isError() {
        if (courseEndDate.isBefore(courseStartDate)) {
            // courseEndDate is before courseStartDate
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    private void addCourse() {
        addCourseButton = findViewById(R.id.addCourseBTN);
        // Set an on-click listener
        addCourseButton.setOnClickListener(v -> {
            courseName = courseNameEdit.getText().toString();
            String courseNote = courseNoteEdit.getText().toString();
            // checks for null values
            if (isNull()) {
                return;
            }
            // Check for end date before start date
            if (isError()) {
                return;
            }
            // Get the selected term ID from the database using termIndex
            int termId = termList.get(termIndex).getTermID();
            int instructorId = instructorList.get(instructorIndex).getInstructorID();

            // If all fields are filled in, add the new term to the database.
            createCourse = new Course(null, courseName, courseStartDate, courseEndDate, termId, instructorId, courseStatus, courseNote);
            database.courseDAO().insertAll(createCourse);
            Toast.makeText(AddCoursesActivity.this, "Course was added successfully", Toast.LENGTH_SHORT).show();
            // Finish the current activity
            finish();
            // Send the user back to the previous TermCoursesActivity with the refreshed data.
            Intent intent = new Intent(AddCoursesActivity.this, TermCoursesActivity.class);
            // Pass the term ID back to TermCoursesActivity
            intent.putExtra("TERM_ID", getIntent().getIntExtra("TERM_ID", -1));
            startActivity(intent);
        });
    }
}
