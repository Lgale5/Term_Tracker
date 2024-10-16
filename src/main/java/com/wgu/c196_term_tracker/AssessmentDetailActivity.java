package com.wgu.c196_term_tracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wgu.c196_term_tracker.utilities.AlertReceiver;
import com.wgu.c196_term_tracker.utilities.NotificationUtils;

import java.time.ZoneId;
import java.time.LocalTime;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import android.Manifest;

import database.AppDatabase;
import database.Converters;
import entities.Assessment;

public class AssessmentDetailActivity extends AppCompatActivity {

    TextView assessmentNameTextView;
    TextView assessmentTypeTextView;
    TextView assessmentStartDateTextView;
    TextView assessmentEndDateTextView;
    AppDatabase database;
    Assessment assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        NotificationUtils.createNotificationChannel(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
        populateView();
    }

    // Method for back button
    @Override
    public boolean onSupportNavigateUp() {
        // Navigate back to the CourseDetailActivity
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("COURSE_ID", assessment.getCourseId());
        startActivity(intent);
        finish();
        return true;
    }

    private void populateView() {
        assessmentNameTextView = findViewById(R.id.assessmentNameTextView);
        assessmentTypeTextView = findViewById(R.id.assessmentTypeTextView);
        assessmentEndDateTextView = findViewById(R.id.assessmentEndDateTextView);
        assessmentStartDateTextView = findViewById(R.id.assessmentStartDateTextView);

        loadAssessmentData();

        FloatingActionButton createAlertButton = findViewById(R.id.createAssessmentAlertButton);
        createAlertButton.setOnClickListener(v -> {
            scheduleAssessmentAlerts();
            Toast.makeText(this, "Alerts created for assessment start and end dates", Toast.LENGTH_SHORT).show();
        });
    }

    // Loads the assessment data into the activity
    private void loadAssessmentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ASSESSMENT_ID")) {
            int assessmentId = intent.getIntExtra("ASSESSMENT_ID", -1);
            if (assessmentId != -1) {
                assessment = database.assessmentDAO().getAssessmentById(assessmentId);
                if (assessment != null) {
                    assessmentNameTextView.setText(assessment.getAssessmentName());
                    assessmentTypeTextView.setText(Converters.fromAssessmentType(assessment.getAssessmentType()));
                    assessmentStartDateTextView.setText(assessment.getStartDate().toString());
                    assessmentEndDateTextView.setText(assessment.getEndDate().toString());
                }
            }
        }
    }

    // Creates the action bar options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_assessments, menu);
        return true;
    }

    // Creates the options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_assessment) {
            editAssessment();
            return true;
        } else if (item.getItemId() == R.id.action_delete_assessment) {
            confirmDeleteAssessment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to edit the assessment
    private void editAssessment() {
        Intent intent = new Intent(AssessmentDetailActivity.this, EditAssessmentActivity.class);
        intent.putExtra("ASSESSMENT_ID", assessment.getAssessmentID());
        startActivity(intent);
    }

    // Method to confirm and delete the assessment
    private void confirmDeleteAssessment() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Assessment")
                .setMessage("Are you sure you want to delete this assessment?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Delete the assessment
                    database.assessmentDAO().delete(assessment);
                    Toast.makeText(this, "Assessment deleted", Toast.LENGTH_SHORT).show();
                    // Navigate back to the CourseDetailActivity
                    Intent intent = new Intent(AssessmentDetailActivity.this, CourseDetailActivity.class);
                    intent.putExtra("COURSE_ID", assessment.getCourseId());
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    // Schedules the assessment alert at start/end dates at 6AM Local
    private void scheduleAssessmentAlerts() {
        String assessmentName = assessment.getAssessmentName();
        long startTime = assessment.getStartDate().atTime(LocalTime.of(6, 0)).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        long endTime = assessment.getEndDate().atTime(LocalTime.of(6, 0)).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;

        setAlarm(this, "Assessment Start Alert", "Assessment " + assessment.getAssessmentName() + " is starting today!", startTime, assessment.getAssessmentID());
        setAlarm(this, "Assessment End Alert", "Assessment " + assessment.getAssessmentName() + " is ending today!", endTime, assessment.getAssessmentID() + 1);


        // Use short intervals for testing
        /* long currentTime = System.currentTimeMillis();
        long startTime = currentTime + 30000; // 30 seconds from now
        long endTime = currentTime + 60000; // 60 seconds from now

        setAlarm(this, "Assessment Start Alert", "Assessment " + assessmentName + " is starting in 30 seconds!", startTime, assessment.getAssessmentID());
        setAlarm(this, "Assessment End Alert", "Assessment " + assessmentName + " is ending in 60 seconds!", endTime, assessment.getAssessmentID() + 1);
        */

    }

    // Sets an alarm that triggers a broadcast to the AlertReceiver.
    private void setAlarm(Context context, String title, String message, long triggerTime, int requestCode) {
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

}