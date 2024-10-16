package com.wgu.c196_term_tracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import adapters.CourseAdapter;
import adapters.TermAdapter;
import database.AppDatabase;
import entities.Course;
import entities.Term;

public class TermCoursesActivity extends AppCompatActivity implements CourseAdapter.OnCourseListener {
    TextView termName;
    Term term;
    AppDatabase database;
    List<Course> courses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_courses);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());

        // Receive the term ID from the intent extra
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("TERM_ID")) {
            int termId = intent.getIntExtra("TERM_ID", -1);
            if (termId != -1) {
                // Use the term ID to fetch the term details from the database
                term = database.termDAO().getTermById(termId);
                if (term != null) {
                    // Populate views with term details
                    populateViews(term);
                    // Create the view
                    courseRecyclerView();
                }
            }
        }
    }


    private void courseRecyclerView() {
        RecyclerView RecyclerView = findViewById(R.id.coursesRecyclerView);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerView.addItemDecoration(decoration);
        // Fetch courses for the selected term from the database
        courses = database.courseDAO().getCoursesByTermId(term.getTermID());
        CourseAdapter courseListAdapter = new CourseAdapter(this, courses, this);
        RecyclerView.setAdapter(courseListAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainTermActivity.class);
        startActivity(intent);
        return true;
    }



    private void populateViews(Term term) {
        this.term = term;
        termName = findViewById(R.id.detailsTermName);
        termName.setText(term.getTermName());
    }

    // Creates the action bar options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_term_courses, menu);
        return true;
    }

    // creates the options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_course) {
            showEditCourseDialog();
            return true;
        } else if (item.getItemId() == R.id.action_delete_term) {
            showDeleteCourseDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to create the edit option button with an array of courses to choose from
    private void showEditCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Course to Edit");

        // Convert the courses list from the db to an array of course names
        String[] courseNamesArray = new String[courses.size()];
        for (int i = 0; i < courses.size(); i++) {
            courseNamesArray[i] = courses.get(i).getCourseName();
        }

        // sets each course to a selectable option and opens the EditCourseActivity for that course.
        builder.setItems(courseNamesArray, (dialog, which) -> {
            Course selectedCourse = courses.get(which);
            Intent intent = new Intent(TermCoursesActivity.this, EditCourseActivity.class);
            intent.putExtra("COURSE_ID", selectedCourse.getCourseID());
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to create the delete option button with an array of courses to choose from
    private void showDeleteCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Course to Delete");

        // Convert the courses list from the database to an array of course names
        String[] courseNamesArray = new String[courses.size()];
        for (int i = 0; i < courses.size(); i++) {
            courseNamesArray[i] = courses.get(i).getCourseName();
        }

        // sets each course to a selectable option and deletes the selected course.
        builder.setItems(courseNamesArray, (dialog, which) -> {
            Course selectedCourse = courses.get(which);
            confirmDeleteCourse(selectedCourse);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to confirm and delete a course
    private void confirmDeleteCourse(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete the course: " + course.getCourseName() + "?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Delete the course
                    database.courseDAO().delete(course);
                    // Refresh the course RecyclerView
                    courseRecyclerView();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onCourseClick(int position) {
        Course selectedCourse = courses.get(position);
        Intent intent = new Intent(TermCoursesActivity.this, CourseDetailActivity.class);
        intent.putExtra("COURSE_ID", selectedCourse.getCourseID());
        startActivity(intent);
    }

    public void addCourseButtonClick(View view) {
        Intent intent = new Intent(this, AddCoursesActivity.class);
        // Pass the term ID to AddCoursesActivity
        intent.putExtra("TERM_ID", term.getTermID());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload course data when the activity resumes
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("TERM_ID")) {
            int termId = intent.getIntExtra("TERM_ID", -1);
            if (termId != -1) {
                // Fetch the term details from the database
                term = database.termDAO().getTermById(termId);
                if (term != null) {
                    // Populate views with term details
                    populateViews(term);
                    // Refresh the course RecyclerView
                    courseRecyclerView();
                }
            }
        }
    }

}