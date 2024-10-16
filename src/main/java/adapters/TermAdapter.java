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

import entities.Term;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {
    private final List<Term> termsList;
    private final Context context;
    private final OnTermListener mOnTermListener;
    DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE;

    public TermAdapter(Context context, List<Term> termsList, OnTermListener mOnTermListener) {
        this.context = context;
        this.termsList = termsList;
        this.mOnTermListener = mOnTermListener;

    }

    public static class TermViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView columnTermName;
        private final TextView columnStartDate;
        private final TextView columnEndDate;
        OnTermListener onTermListener;

        public TermViewHolder(View view, OnTermListener onTermListener) {
            super(view);
            columnTermName=itemView.findViewById(R.id.columnTermName);
            columnStartDate=itemView.findViewById(R.id.columnStartDate);
            columnEndDate=itemView.findViewById(R.id.columnEndDate);
            this.onTermListener = onTermListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTermListener.onTermClick(getAbsoluteAdapterPosition());
        }
    }

    @NonNull
    @Override
    public TermAdapter.TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.term_list, parent, false);

        return new TermViewHolder(view, mOnTermListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TermAdapter.TermViewHolder holder, int position) {
        holder.columnTermName.setText(this.termsList.get(position).termName);
        holder.columnStartDate.setText(this.termsList.get(position).startDate.format(format));
        holder.columnEndDate.setText(this.termsList.get(position).endDate.format(format));
    }

    @Override
    public int getItemCount() {
        if (termsList!=null) {
            return termsList.size();
        }
        else return 0;
    }

    public interface OnTermListener{
        void onTermClick(int position);
    }

}
