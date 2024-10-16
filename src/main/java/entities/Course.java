package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import database.Converters;

@Entity(tableName = "course", foreignKeys = {
        @ForeignKey(entity = Term.class, parentColumns = "termID", childColumns = "termId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Instructor.class, parentColumns = "instructorID", childColumns = "instructorId", onDelete = ForeignKey.SET_NULL)
})
@TypeConverters(Converters.class)
public class Course {
    @PrimaryKey(autoGenerate = true)
    public Integer courseID;

    @ColumnInfo(name = "course_name")
    public String courseName;

    @ColumnInfo(name = "start_date")
    public LocalDate startDate;

    @ColumnInfo(name = "end_date")
    public LocalDate endDate;

    // Foreign key referencing the term table
    @ColumnInfo(index = true)
    public int termId;

    @ColumnInfo(name = "status")
    public CourseStatus status;

    // Foreign key referencing the instructor table
    @ColumnInfo(index = true)
    public Integer instructorId;

    @ColumnInfo(name = "course_note")
    public String courseNote;

    public Course(Integer courseID, String courseName, LocalDate startDate, LocalDate endDate, int termId, Integer instructorId, CourseStatus status, String courseNote) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.termId = termId;
        this.status = status;
        this.instructorId = instructorId;
        this.courseNote = courseNote;
    }

    public Integer getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTermId() {
        return termId;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public String getCourseNote() { return courseNote; }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public void setCourseNote(String courseNote) { this.courseNote = courseNote; }

    @NonNull
    @Override
    public String toString() {
        return "Course{" +
                "courseID=" + courseID +
                ", courseName='" + courseName + '\'' +
                ", startDate=" + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                ", endDate=" + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                ", termId=" + termId +
                ", instructorId=" + instructorId +
                ", status=" + status +
                ", courseNote='" + courseNote + '\'' +
                '}';
    }
}
