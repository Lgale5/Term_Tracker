package com.wgu.c196_term_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import database.AppDatabase;
import entities.Course;
import entities.Term;
import entities.CourseStatus;
import entities.Instructor;

public class EditCourseActivity extends AppCompatActivity {
    EditText courseNameEdit;
    EditText courseNoteEdit;
    Button courseStartEditBTN;
    Button courseEndEditBTN;
    Button termSelectEditBTN;
    Button statusSelectEditBTN;
    Button instructorSelectEditBTN;
    Button saveButton;
    Course course;
    LocalDate courseStartDate;
    LocalDate courseEndDate;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    Calendar calendar;
    DatePickerDialog picker;
    AppDatabase database;
    List<Term> termList;
    List<Instructor> instructorList;
    String[] termNames;
    String[] instructorNames;
    int termIndex;
    int instructorIndex;
    int statusIndex;
    CourseStatus courseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        termList = database.termDAO().getAllTerms();
        instructorList = database.instructorDAO().getAllInstructors();
        populateView();
    }

    private void populateView() {
        courseNameEdit = findViewById(R.id.editCourseNameText);
        courseStartEditBTN = findViewById(R.id.courseStartEditBTN);
        courseEndEditBTN = findViewById(R.id.courseEndEditBTN);
        termSelectEditBTN = findViewById(R.id.termSelectEditBTN);
        statusSelectEditBTN = findViewById(R.id.statusSelectEditBTN);
        instructorSelectEditBTN = findViewById(R.id.instructorSelectEditBTN);
        courseNoteEdit = findViewById(R.id.editCourseNoteText);
        saveButton = findViewById(R.id.saveBTN);

        startDatePicker();
        endDatePicker();
        populateTermSpinner();
        populateStatusSpinner();
        populateInstructorSpinner();
        loadCourseData();
        saveCourse();
    }

    // Method for back button
    @Override
    public boolean onSupportNavigateUp() {
        // Finish the current activity and return to the previous one
        finish();
        return true;
    }

    // method to receive the course ID from the intent and sets the data in the fields
    private void loadCourseData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("COURSE_ID")) {
            int courseId = intent.getIntExtra("COURSE_ID", -1);
            if (courseId != -1) {
                course = database.courseDAO().getCourseById(courseId);
                if (course != null) {
                    courseNameEdit.setText(course.getCourseName());
                    courseStartEditBTN.setText(course.getStartDate().format(formatter));
                    courseEndEditBTN.setText(course.getEndDate().format(formatter));
                    courseStartDate = course.getStartDate();
                    courseEndDate = course.getEndDate();
                    courseNoteEdit.setText(course.getCourseNote());

                    // Load term into form
                    if (course.getTermId() != 0) {
                        Term term = database.termDAO().getTermById(course.getTermId());
                        if (term != null) {
                            termSelectEditBTN.setText(term.getTermName());
                            for (int i = 0; i < termList.size(); i++) {
                                if (termList.get(i).getTermID() == term.getTermID()) {
                                    termIndex = i;
                                    break;
                                }
                            }
                        }
                    }

                    // loads instructor into form
                    if (course.getInstructorId() != null) {
                        Instructor instructor = database.instructorDAO().getInstructorById(course.getInstructorId());
                        if (instructor != null) {
                            instructorSelectEditBTN.setText(instructor.getName());
                            for (int i = 0; i < instructorList.size(); i++) {
                                if (instructorList.get(i).getInstructorID() == instructor.getInstructorID()) {
                                    instructorIndex = i;
                                    break;
                                }
                            }
                        }
                    }

                    // Load status into form
                    if (course.getStatus() != null) {
                        CourseStatus status = course.getStatus();
                        statusSelectEditBTN.setText(status.name());
                        courseStatus = status; // Assigns the status to courseStatus
                        switch (status) {
                            case InProgress:
                                statusIndex = 0;
                                break;
                            case Completed:
                                statusIndex = 1;
                                break;
                            case Dropped:
                                statusIndex = 2;
                                break;
                            case PlanToTake:
                                statusIndex = 3;
                                break;
                        }
                    }

                }
            }
        }
    }

    // Creates start date picker
    private void startDatePicker() {
        courseStartEditBTN.setOnClickListener(v -> {
            // Set values for the calendar
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(EditCourseActivity.this, (view, year1, month1, dayOfMonth) -> {
                courseStartDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                courseStartEditBTN.setText(formatter.format(courseStartDate));
            }, year, month, day);
            picker.show();
        });
    }

    // Creates end date picker
    private void endDatePicker() {
        courseEndEditBTN.setOnClickListener(v -> {
            // Set values for the calendar
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // Create a date picker dialog
            picker = new DatePickerDialog(EditCourseActivity.this, (view, year1, month1, dayOfMonth) -> {
                courseEndDate = LocalDate.of(year1, (month1 + 1), dayOfMonth);
                courseEndEditBTN.setText(formatter.format(courseEndDate));
            }, year, month, day);
            picker.show();
        });
    }

    // sets up the term spinner
    private void populateTermSpinner() {
        termSelectEditBTN.setOnClickListener(v -> {
            // Populate spinner by database created string array
            termNames = new String[termList.size()];
            for (int i = 0; i < termList.size(); i++) {
                termNames[i] = termList.get(i).getTermName();
            }

            AtomicInteger selected = new AtomicInteger(-1);

            // Creates the term selector AlertDialog
            AlertDialog termDialog = new AlertDialog.Builder(EditCourseActivity.this)
                    .setSingleChoiceItems(termNames, selected.get(),
                            (dialog, which) -> {
                                // Handle term selection
                                termSelectEditBTN.setText(termNames[which]);
                                selected.set(which);
                                termIndex = which;
                                // Dismiss the dialog
                                dialog.dismiss();
                            })
                    .setTitle("Select Term")
                    .create();

            termDialog.show();
        });
    }

    private void populateStatusSpinner() {
        statusSelectEditBTN.setOnClickListener(v -> {
            String[] statuses = {"In Progress", "Completed", "Dropped", "Plan to Take"};

            AtomicInteger selected = new AtomicInteger(-1);

            // Create the AlertDialog
            AlertDialog statusDialog = new AlertDialog.Builder(EditCourseActivity.this)
                    .setSingleChoiceItems(statuses, selected.get(),
                            (dialog, which) -> {
                                // Handle status selection
                                statusSelectEditBTN.setText(statuses[which]);
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

            statusDialog.show();
        });
    }

    private void populateInstructorSpinner() {
        instructorSelectEditBTN.setOnClickListener(v -> {
            instructorNames = new String[instructorList.size()];
            for (int i = 0; i < instructorList.size(); i++) {
                instructorNames[i] = instructorList.get(i).getName();
            }

            AtomicInteger selected = new AtomicInteger(-1);
            AlertDialog instructorDialog = new AlertDialog.Builder(EditCourseActivity.this)
                    .setSingleChoiceItems(instructorNames, selected.get(),
                            (dialog, which) -> {
                                instructorSelectEditBTN.setText(instructorNames[which]);
                                selected.set(which);
                                instructorIndex = which;
                                dialog.dismiss();
                            })
                    .setTitle("Select Instructor")
                    .create();
            instructorDialog.show();
        });
    }

    private void saveCourse() {
        saveButton.setOnClickListener(v -> {
            String courseName = courseNameEdit.getText().toString();
            String courseNote = courseNoteEdit.getText().toString();
            // Check for null values
            if (isNull()) {
                return;
            }
            // Check for end date before start date
            if (isError()) {
                return;
            }
            // Update the course in the database
            course.setCourseName(courseName);
            course.setCourseNote(courseNote);
            course.setStartDate(courseStartDate);
            course.setEndDate(courseEndDate);
            course.setStatus(courseStatus);
            // Update term if selected from spinner
            if (termIndex != -1) {
                course.setTermId(termList.get(termIndex).getTermID());
            }
            if (instructorIndex != -1) {
                course.setInstructorId(instructorList.get(instructorIndex).getInstructorID());
            }

            try {
                database.courseDAO().update(course);
                Toast.makeText(EditCourseActivity.this, "Course was updated successfully", Toast.LENGTH_SHORT).show();
                // Send the user back to the TermCoursesActivity
                Intent intent = new Intent(EditCourseActivity.this, TermCoursesActivity.class);
                intent.putExtra("TERM_ID", course.getTermId());
                startActivity(intent);
            } catch (SQLiteException e) {
                Toast.makeText(EditCourseActivity.this, "Error updating course: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
                // Handle other exceptions
            }
        });
    }

    // Errors for null values
    private boolean isNull() {
        if (courseNameEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter a course name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (courseStartDate == null) {
            Toast.makeText(this, "You must select a start date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (courseEndDate == null) {
            Toast.makeText(this, "You must select an end date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (termIndex < 0) {
            Toast.makeText(this, "Please select a term", Toast.LENGTH_SHORT).show();
            return true;
        } else if (courseStatus == null) {
            Toast.makeText(this, "You must select a status", Toast.LENGTH_SHORT).show();
            return true;
        } else if (instructorIndex < 0) {
            Toast.makeText(this, "Please select an instructor", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    private boolean isError() {
        if (courseEndDate.isBefore(courseStartDate)) {
            // courseEndDate is before courseStartDate
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

}