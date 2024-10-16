package com.wgu.c196_term_tracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;
import database.AppDatabase;
import entities.Instructor;

public class EditInstructorActivity extends AppCompatActivity {
    EditText instructorNameEdit;
    EditText instructorPhoneEdit;
    EditText instructorEmailEdit;
    String instructorName;
    String instructorPhone;
    String instructorEmail;
    Button saveInstructorButton;
    AppDatabase database;
    Instructor instructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instructor);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        populateView();
        loadInstructorData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void populateView() {
        instructorNameEdit = findViewById(R.id.instructorNameEditText);
        instructorPhoneEdit = findViewById(R.id.instructorPhoneEditText);
        instructorEmailEdit = findViewById(R.id.instructorEmailEditText);
        saveInstructorButton = findViewById(R.id.saveInstructorButton);
        saveInstructorButton.setOnClickListener(v -> saveInstructor());
    }

    private void loadInstructorData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("INSTRUCTOR_ID")) {
            int instructorId = intent.getIntExtra("INSTRUCTOR_ID", -1);
            if (instructorId != -1) {
                instructor = database.instructorDAO().getInstructorById(instructorId);
                if (instructor != null) {
                    instructorNameEdit.setText(instructor.getName());
                    instructorPhoneEdit.setText(instructor.getPhone());
                    instructorEmailEdit.setText(instructor.getEmail());
                }
            }
        }
    }

    private boolean isNull() {
        if (instructorNameEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please add an instructor name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (instructorPhoneEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please add an instructor phone", Toast.LENGTH_SHORT).show();
            return true;
        } else if (instructorEmailEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please add an instructor email", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private void saveInstructor() {
        instructorName = instructorNameEdit.getText().toString();
        instructorPhone = instructorPhoneEdit.getText().toString();
        instructorEmail = instructorEmailEdit.getText().toString();

        // checks for null values
        if (isNull()) {
            return;
        }

        // If all fields are filled in, update the instructor in the database.
        instructor.setName(instructorName);
        instructor.setPhone(instructorPhone);
        instructor.setEmail(instructorEmail);
        database.instructorDAO().update(instructor);

        Toast.makeText(EditInstructorActivity.this, "Instructor was updated successfully", Toast.LENGTH_SHORT).show();
        // Send the user back to the previous InstructorsActivity with the refreshed data.
        Intent intent = new Intent(EditInstructorActivity.this, InstructorsActivity.class);
        startActivity(intent);
        finish();
    }
}