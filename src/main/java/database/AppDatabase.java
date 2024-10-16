package database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import dao.AssessmentDAO;
import dao.CourseDAO;
import dao.InstructorDAO;
import dao.TermDAO;
import entities.Assessment;
import entities.Course;
import entities.Instructor;
import entities.Term;

@Database(entities = {Term.class, Course.class, Instructor.class, Assessment.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "app.db";
    private static AppDatabase appDatabase;
    public abstract TermDAO termDAO();
    public abstract CourseDAO courseDAO();
    public abstract InstructorDAO instructorDAO();
    public abstract AssessmentDAO assessmentDAO();

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }
}
