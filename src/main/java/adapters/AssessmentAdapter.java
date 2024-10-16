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

import entities.Assessment;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {
    private final List<Assessment> assessmentList;
    private final Context context;
    private final OnAssessmentListener mOnAssessmentListener;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public AssessmentAdapter(Context context, List<Assessment> assessmentList, OnAssessmentListener mOnAssessmentListener) {
        this.context = context;
        this.assessmentList = assessmentList;
        this.mOnAssessmentListener = mOnAssessmentListener;
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView assessmentName;
        OnAssessmentListener onAssessmentListener;

        public AssessmentViewHolder(View view, OnAssessmentListener onAssessmentListener) {
            super(view);
            assessmentName = itemView.findViewById(R.id.assessmentName);
            this.onAssessmentListener = onAssessmentListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAssessmentListener.onAssessmentClick(getAbsoluteAdapterPosition());
        }
    }

    @NonNull
    @Override
    public AssessmentAdapter.AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.assessment_list, parent, false);
        return new AssessmentViewHolder(view, mOnAssessmentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentAdapter.AssessmentViewHolder holder, int position) {
        Assessment assessment = assessmentList.get(position);
        holder.assessmentName.setText(assessment.getAssessmentName());
    }

    @Override
    public int getItemCount() {
        return assessmentList != null ? assessmentList.size() : 0;
    }

    public interface OnAssessmentListener {
        void onAssessmentClick(int position);
    }
}
