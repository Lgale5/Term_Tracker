package database;

import androidx.room.TypeConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import entities.AssessmentType;
import entities.CourseStatus;

public class Converters {
    static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static LocalDate fromDateString(String string){
        return LocalDate.parse(string, formatter);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date){
        return formatter.format(date);
    }

    @TypeConverter
    public static String fromCourseStatus(CourseStatus status) {
        return status.name();
    }

    @TypeConverter
    public static CourseStatus fromStatusString(String s) {
        switch (s.toLowerCase()) {
            case "inprogress":
                return CourseStatus.InProgress;
            case "completed":
                return CourseStatus.Completed;
            case "dropped":
                return CourseStatus.Dropped;
            case "plantotake":
                return CourseStatus.PlanToTake;
            default:
                return null;
        }
    }

    @TypeConverter
    public static String fromAssessmentType(AssessmentType type) {
        return type.name();
    }

    @TypeConverter
    public static AssessmentType fromAssessmentTypeString(String s) {
        switch (s.toLowerCase()) {
            case "performance":
                return AssessmentType.Performance;
            case "objective":
                return AssessmentType.Objective;
            default:
                return null;
        }
    }

}


