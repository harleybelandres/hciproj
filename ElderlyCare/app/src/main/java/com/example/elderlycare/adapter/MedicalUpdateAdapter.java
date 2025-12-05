package com.example.elderlycare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.elderlycare.R;
import com.example.elderlycare.model.MedicalUpdate;
import java.util.List;

public class MedicalUpdateAdapter extends RecyclerView.Adapter<MedicalUpdateAdapter.ViewHolder> {

    private List<MedicalUpdate> updates;

    public MedicalUpdateAdapter(List<MedicalUpdate> updates) {
        this.updates = updates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_update, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalUpdate update = updates.get(position);
        holder.tvTitle.setText(update.getTitle());
        holder.tvMessage.setText(update.getMessage());
        holder.tvTimestamp.setText(update.getTimestamp().toDate().toString());
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvUpdateTitle);
            tvMessage = itemView.findViewById(R.id.tvUpdateMessage);
            tvTimestamp = itemView.findViewById(R.id.tvUpdateTimestamp);
        }
    }
}
