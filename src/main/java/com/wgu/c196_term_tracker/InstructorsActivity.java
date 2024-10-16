package com.wgu.c196_term_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import adapters.InstructorAdapter;
import database.AppDatabase;
import entities.Instructor;

public class InstructorsActivity extends AppCompatActivity {
    List<Instructor> instructors;
    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructors);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        instructors = database.instructorDAO().getAllInstructors();
        instructorRecyclerView();
    }

    private void instructorRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.instructorsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        InstructorAdapter instructorAdapter = new InstructorAdapter(this, instructors);
        recyclerView.setAdapter(instructorAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    // Creates the action bar options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_instructors, menu);
        return true;
    }

    // creates the options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_instructor) {
            showEditInstructorDialog();
            return true;
        } else if (item.getItemId() == R.id.action_delete_instructor) {
            showDeleteInstructorDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to create the edit option button with an array of instructors to choose from
    private void showEditInstructorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an Instructor to Edit");

        String[] instructorNamesArray = new String[instructors.size()];
        for (int i = 0; i < instructors.size(); i++) {
            instructorNamesArray[i] = instructors.get(i).getName();
        }

        builder.setItems(instructorNamesArray, (dialog, which) -> {
            Instructor selectedInstructor = instructors.get(which);
            Intent intent = new Intent(InstructorsActivity.this, EditInstructorActivity.class);
            intent.putExtra("INSTRUCTOR_ID", selectedInstructor.getInstructorID());
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to create the delete option button with an array of instructors to choose from
    private void showDeleteInstructorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an Instructor to Delete");

        String[] instructorNamesArray = new String[instructors.size()];
        for (int i = 0; i < instructors.size(); i++) {
            instructorNamesArray[i] = instructors.get(i).getName();
        }

        builder.setItems(instructorNamesArray, (dialog, which) -> {
            Instructor selectedInstructor = instructors.get(which);
            confirmDeleteInstructor(selectedInstructor);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // confirms with the users request
    private void confirmDeleteInstructor(Instructor instructor) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Instructor")
                .setMessage("Are you sure you want to delete this instructor?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    database.instructorDAO().delete(instructor);
                    instructors.remove(instructor);
                    instructorRecyclerView();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void addInstructorButtonClick(View view) {
        Intent intent = new Intent(this, AddInstructorActivity.class);
        startActivity(intent);
    }
}