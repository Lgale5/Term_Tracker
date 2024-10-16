package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Instructor;

@Dao
public interface InstructorDAO {
    @Insert
    void insert(Instructor instructor);

    @Update
    void update(Instructor instructor);

    @Delete
    void delete(Instructor instructor);

    @Query("SELECT * FROM instructor")
    List<Instructor> getAllInstructors();

    @Query("SELECT * FROM instructor WHERE instructorID = :instructorId")
    Instructor getInstructorById(int instructorId);
}
