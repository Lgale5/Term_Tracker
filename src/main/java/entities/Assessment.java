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

@Entity(tableName = "assessment", foreignKeys = {
        @ForeignKey(entity = Course.class, parentColumns = "courseID", childColumns = "courseId", onDelete = ForeignKey.CASCADE)
})
@TypeConverters(Converters.class)
public class Assessment {

    @PrimaryKey(autoGenerate = true)
    public Integer assessmentID;

    @ColumnInfo(name = "assessment_name")
    public String assessmentName;

    @ColumnInfo(name = "assessment_type")
    public AssessmentType assessmentType;

    @ColumnInfo(name = "start_date")
    public LocalDate startDate;

    @ColumnInfo(name = "end_date")
    public LocalDate endDate;

    @ColumnInfo(index = true)
    public int courseId;

    public Assessment(Integer assessmentID, String assessmentName, LocalDate startDate, AssessmentType assessmentType, LocalDate endDate, int courseId) {
        this.assessmentID = assessmentID;
        this.assessmentName = assessmentName;
        this.assessmentType = assessmentType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseId = courseId;
    }

    public Integer getAssessmentID() {
        return assessmentID;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Assessment{" +
                "assessmentID=" + assessmentID +
                ", assessmentName='" + assessmentName + '\'' +
                ", assessmentType=" + assessmentType +
                ", startDate=" + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                ", endDate=" + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                ", courseId=" + courseId +
                '}';
    }
}
