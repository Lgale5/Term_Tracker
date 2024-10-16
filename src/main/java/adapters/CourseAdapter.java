package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wgu.c196_term_tracker.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

import database.AppDatabase;
import entities.Course;
import entities.Instructor;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private final List<Course> courseList;
    private final Context context;
    private final OnCourseListener mOnCourseListener;
    DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE;
    private final AppDatabase database;

    public CourseAdapter(Context context, List<Course> courseList, OnCourseListener mOnCourseListener) {
        this.context = context;
        this.courseList = courseList;
        this.mOnCourseListener = mOnCourseListener;
        this.database = AppDatabase.getInstance(context);
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView courseName;
        private final TextView startDate;
        private final TextView endDate;
        private final TextView status;
        private final TextView instructorName;
        OnCourseListener onCourseListener;

        public CourseViewHolder(View view, OnCourseListener onCourseListener) {
            super(view);
            courseName = itemView.findViewById(R.id.courseName);
            startDate = itemView.findViewById(R.id.courseStartDate);
            endDate = itemView.findViewById(R.id.courseEndDate);
            status = itemView.findViewById(R.id.courseStatus);
            instructorName = itemView.findViewById(R.id.instructorName);
            this.onCourseListener = onCourseListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCourseListener.onCourseClick(getAbsoluteAdapterPosition());
        }
    }

    @NonNull
    @Override
    public CourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_list, parent, false);
        return new CourseViewHolder(view, mOnCourseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.courseName);
        holder.startDate.setText(course.startDate.format(format));
        holder.endDate.setText(course.endDate.format(format));
        holder.status.setText(course.status.name());

        // Fetch the instructor for the current course
        if (course.getInstructorId() != null) {
            Instructor instructor = database.instructorDAO().getInstructorById(course.getInstructorId());
            holder.instructorName.setText(instructor != null ? instructor.getName() : "No Instructor");
        } else {
            holder.instructorName.setText("No Instructor");
        }
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public interface OnCourseListener {
        void onCourseClick(int position);
    }
}
