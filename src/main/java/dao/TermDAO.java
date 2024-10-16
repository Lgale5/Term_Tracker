package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Term;

@Dao
public interface TermDAO {
    @Query("SELECT * FROM term")
    List<Term> getAllTerms();
    @Insert
    void insertAll(Term... terms);
    @Update
    void update(Term term);

    @Delete
    void delete(Term term);

    @Query("SELECT term_name FROM term")
    String[] getTermsArray();

    @Query("SELECT * FROM term WHERE termID = :id")
    Term getTermById(int id);

}

