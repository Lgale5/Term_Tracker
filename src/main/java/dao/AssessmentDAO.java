package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Assessment;

@Dao
public interface AssessmentDAO {

    @Query("SELECT * FROM assessment WHERE courseId = :courseId")
    List<Assessment> getAssessmentsByCourseId(int courseId);

    @Insert
    void insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM assessment WHERE assessmentID = :assessmentId")
    Assessment getAssessmentById(int assessmentId);

    @Query("SELECT * FROM assessment")
    List<Assessment> getAllAssessments();
}
