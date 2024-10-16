package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wgu.c196_term_tracker.R;

import java.util.List;

import entities.Instructor;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {
    private final List<Instructor> instructorList;
    private final Context context;

    public InstructorAdapter(Context context, List<Instructor> instructorList) {
        this.context = context;
        this.instructorList = instructorList;
    }


    public static class InstructorViewHolder extends RecyclerView.ViewHolder {
        private final TextView instructorNameTextView;
        private final TextView instructorPhoneTextView;
        private final TextView instructorEmailTextView;

        public InstructorViewHolder(View view) {
            super(view);
            instructorNameTextView = itemView.findViewById(R.id.instructorNameTextView);
            instructorPhoneTextView = itemView.findViewById(R.id.instructorPhoneTextView);
            instructorEmailTextView = itemView.findViewById(R.id.instructorEmailTextView);
        }
    }

    @NonNull
    @Override
    public InstructorAdapter.InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.instructor_list, parent, false);
        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorAdapter.InstructorViewHolder holder, int position) {
        holder.instructorNameTextView.setText(this.instructorList.get(position).getName());
        holder.instructorPhoneTextView.setText(this.instructorList.get(position).getPhone());
        holder.instructorEmailTextView.setText(this.instructorList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        if (instructorList != null) {
            return instructorList.size();
        } else {
            return 0;
        }
    }
}
