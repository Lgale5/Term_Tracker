package com.wgu.c196_term_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import adapters.TermAdapter;
import database.AppDatabase;
import entities.Course;
import entities.Term;

public class MainTermActivity extends AppCompatActivity implements TermAdapter.OnTermListener {
    List<Term> terms;
    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_term);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        terms = database.termDAO().getAllTerms();

        termRecyclerView();

    }

    private void termRecyclerView() {
        RecyclerView RecyclerView = findViewById(R.id.termsRecyclerView);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerView.addItemDecoration(decoration);
        TermAdapter termListAdapter = new TermAdapter(this, terms, this);
        RecyclerView.setAdapter(termListAdapter);
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
        inflater.inflate(R.menu.menu_main_term, menu);
        return true;
    }

    // creates the options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_term) {
            showEditTermDialog();
            return true;
        } else if (item.getItemId() == R.id.action_delete_term) {
            showDeleteTermDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to create the edit option button with an array of terms to choose from
    private void showEditTermDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Term to Edit");

        // Convert the terms list from the db to an array of term names
        String[] termNamesArray = new String[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            termNamesArray[i] = terms.get(i).getTermName();
        }

        // sets each term to a selectable option and opens the EditTermActivity for that term.
        builder.setItems(termNamesArray, (dialog, which) -> {
            // which is the index of the selected term
            Term selectedTerm = terms.get(which);
            Intent intent = new Intent(MainTermActivity.this, EditTermActivity.class);
            intent.putExtra("TERM_ID", selectedTerm.getTermID());
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to create the delete option button with an array of terms to choose from
    private void showDeleteTermDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Term to Delete");

        // Convert the terms list from the database to an array of term names
        String[] termNamesArray = new String[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            termNamesArray[i] = terms.get(i).getTermName();
        }

        // Sets each term to a selectable option and deletes the selected term if it has no associated courses.
        builder.setItems(termNamesArray, (dialog, which) -> {
            Term selectedTerm = terms.get(which);
            confirmDeleteTerm(selectedTerm);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to confirm and delete a term only if there are no associated courses
    private void confirmDeleteTerm(Term term) {
        List<Course> coursesToDelete = database.courseDAO().getCoursesByTermId(term.getTermID());
        if (coursesToDelete.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Term")
                    .setMessage("Are you sure you want to delete the term: " + term.getTermName() + "?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Delete the term
                        database.termDAO().delete(term);
                        // Refresh the term RecyclerView
                        terms = database.termDAO().getAllTerms();
                        termRecyclerView();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Term")
                    .setMessage("This term has courses assigned to it and cannot be deleted.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    // Sets the action when a term is clicked
    @Override
    public void onTermClick(int position) {
        Term selectedTerm = terms.get(position);
        Intent intent = new Intent(this, TermCoursesActivity.class);
        // Sends Term ID to TermDetailsActivity
        intent.putExtra("TERM_ID", selectedTerm.getTermID());
        startActivity(intent);
    }

    public void addTermButtonClick(View view) {
        Intent intent = new Intent(this, AddTermActivity.class);
        startActivity(intent);
    }

}