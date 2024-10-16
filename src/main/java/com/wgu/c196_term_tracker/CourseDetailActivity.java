package com.wgu.c196_term_tracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wgu.c196_term_tracker.utilities.AlertReceiver;
import com.wgu.c196_term_tracker.utilities.NotificationUtils;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import android.Manifest;

import adapters.AssessmentAdapter;
import database.AppDatabase;
import entities.Assessment;
import entities.Course;
import entities.Instructor;

public class CourseDetailActivity extends AppCompatActivity implements AssessmentAdapter.OnAssessmentListener {

    TextView courseNameTextView;
    TextView courseStartTextView;
    TextView courseEndTextView;
    TextView courseStatusTextView;
    TextView instructorNameTextView;
    TextView instructorPhoneTextView;
    TextView instructorEmailTextView;
    TextView courseNoteTextView;
    private RecyclerView assessmentRecyclerView;
    private List<Assessment> assessments;
    AppDatabase database;
    Course course;
    Instructor instructor;
    Button shareNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getInstance(this.getApplicationContext());
        NotificationUtils.createNotificationChannel(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
        populateView();

        FloatingActionButton createAlertButton = findViewById(R.id.createAlertButton);
        createAlertButton.setOnClickListener(v -> {
            scheduleCourseAlerts();
            Toast.makeText(this, "Alerts created for course start and end dates", Toast.LENGTH_SHORT).show();
        });

        // Initialize share note button and set click listener
        shareNoteButton = findViewById(R.id.shareNoteButton);
        shareNoteButton.setOnClickListener(v -> shareNote());

    }

    // Method for back button
    @Override
    public boolean onSupportNavigateUp() {
        // Navigate back to the TermCoursesActivity
        Intent intent = new Intent(this, TermCoursesActivity.class);
        intent.putExtra("TERM_ID", course.getTermId());
        startActivity(intent);
        finish();
        return true;
    }

    private void populateView() {
        courseNameTextView = findViewById(R.id.courseNameTextView);
        courseStartTextView = findViewById(R.id.courseStartTextView);
        courseEndTextView = findViewById(R.id.courseEndTextView);
        courseStatusTextView = findViewById(R.id.courseStatusTextView);
        instructorNameTextView = findViewById(R.id.instructorNameTextView);
        instructorPhoneTextView = findViewById(R.id.instructorPhoneTextView);
        instructorEmailTextView = findViewById(R.id.instructorEmailTextView);
        courseNoteTextView = findViewById(R.id.courseNoteTextView);
        assessmentRecyclerView = findViewById(R.id.assessmentRecyclerView);

        loadCourseData();
    }

    // Loads the course data into the activity
    private void loadCourseData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("COURSE_ID")) {
            int courseId = intent.getIntExtra("COURSE_ID", -1);
            if (courseId != -1) {
                course = database.courseDAO().getCourseById(courseId);
                if (course != null) {
                    courseNameTextView.setText(course.getCourseName());
                    courseStartTextView.setText(course.getStartDate().toString());
                    courseEndTextView.setText(course.getEndDate().toString());
                    courseStatusTextView.setText(course.getStatus().name());
                    courseNoteTextView.setText(course.getCourseNote());

                    assessments = database.assessmentDAO().getAssessmentsByCourseId(courseId);
                    setupRecyclerView();

                    // Load instructor data
                    if (course.getInstructorId() != null) {
                        instructor = database.instructorDAO().getInstructorById(course.getInstructorId());
                        if (instructor != null) {
                            instructorNameTextView.setText(instructor.getName());
                            instructorPhoneTextView.setText(instructor.getPhone());
                            instructorEmailTextView.setText(instructor.getEmail());
                        }
                    }
                }
            }
        }
    }

    // Creates the view for assessments
    private void setupRecyclerView() {
        assessmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        assessmentRecyclerView.addItemDecoration(decoration);
        AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this, assessments, this);
        assessmentRecyclerView.setAdapter(assessmentAdapter);
    }

    // Schedules the Course alert at start/end dates at 6AM Local
    private void scheduleCourseAlerts() {
        String courseName = course.getCourseName();
        long startTime = course.getStartDate().atTime(LocalTime.of(6, 0)).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        long endTime = course.getEndDate().atTime(LocalTime.of(6, 0)).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;

        setAlarm(this, "Course Start Alert", "Course " + course.getCourseName() + " is starting today!", startTime, course.getCourseID());
        setAlarm(this, "Course End Alert", "Course " + course.getCourseName() + " is ending today!", endTime, course.getCourseID() + 1);
    }

        /* Set short intervals for testing
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime + 30000; // 30 seconds from now
        long endTime = currentTime + 60000; // 60 seconds from now

        setAlarm(this, "Course Start Alert", "Course " + courseName + " is starting in 30 seconds!", startTime, course.getCourseID());
        setAlarm(this, "Course End Alert", "Course " + courseName + " is ending in 60 seconds!", endTime, course.getCourseID() + 1);*/

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

    // Method to share course notes if course notes is not empty
    private void shareNote() {
        String courseNote = course.getCourseNote();
        if (courseNote == null || courseNote.isEmpty()) {
            Toast.makeText(this, "No note to share", Toast.LENGTH_SHORT).show();
            return;
        }

        String mimeType = "text/plain";
        String title = "Share Course Note";
        String textToShare = "Course Note: " + courseNote;

        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();
    }


    // Handles the click event for assessments
    @Override
    public void onAssessmentClick(int position) {
        Intent intent = new Intent(this, AssessmentDetailActivity.class);
        Assessment selectedAssessment = assessments.get(position);
        intent.putExtra("ASSESSMENT_ID", selectedAssessment.getAssessmentID());
        startActivity(intent);
    }

    public void addAssessmentButtonClick(View view) {
        Intent intent = new Intent(this, AddAssessmentActivity.class);
        intent.putExtra("COURSE_ID", course.getCourseID());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourseData();
    }
}

