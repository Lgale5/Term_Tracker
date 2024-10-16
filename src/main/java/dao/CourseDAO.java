package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Course;
import entities.Term;

@Dao
public interface CourseDAO {
    @Query("SELECT * FROM course")
    List<Course> getAllCourses();

    @Insert
    void insertAll(Course... courses);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM course WHERE courseID = :courseId")
    Course getCourseById(int courseId);

    @Query("SELECT * FROM course WHERE termId = :termId")
    List<Course> getCoursesByTermId(int termId);

    }
