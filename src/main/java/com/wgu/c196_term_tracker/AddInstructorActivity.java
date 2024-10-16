package com.wgu.c196_term_tracker;

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

import java.util.Objects;

import database.AppDatabase;
import entities.Instructor;

public class AddInstructorActivity extends AppCompatActivity {
    EditText instructorNameEdit;
    EditText instructorPhoneEdit;
    EditText instructorEmailEdit;
    String instructorName;
    String instructorPhone;
    String instructorEmail;
    Button saveInstructorButton;
    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_instructor);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        populateView();
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

        // If all fields are filled in, add the new instructor to the database.
        Instructor createInstructor = new Instructor(null, instructorName, instructorPhone, instructorEmail);
        database.instructorDAO().insert(createInstructor);
        Toast.makeText(AddInstructorActivity.this, "Instructor was added successfully", Toast.LENGTH_SHORT).show();
        // Send the user back to the previous InstructorsActivity with the refreshed data.
        Intent intent = new Intent(AddInstructorActivity.this, InstructorsActivity.class);
        startActivity(intent);
        finish();
    }
}