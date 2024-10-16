package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "term")
public class Term {
    @PrimaryKey(autoGenerate = true)
    public Integer termID;
    @ColumnInfo(name = "term_name")
    public String termName;
    @ColumnInfo(name = "start_date")
    public LocalDate startDate;
    @ColumnInfo(name = "end_date")
    public LocalDate endDate;

    public Term(Integer termID, String termName, LocalDate startDate, LocalDate endDate) {
        this.termID = termID;
        this.termName = termName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getTermID() {
        return termID;
    }

    public String getTermName() {
        return termName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Term{" +
                "termName='" + termName + '\'' +
                ", startDate=" + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                ", endDate=" + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                '}';
    }
}
