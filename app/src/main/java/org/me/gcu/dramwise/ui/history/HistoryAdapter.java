package org.me.gcu.dramwise.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<DrinkEntry> entries = new ArrayList<>();

    public void updateData(List<DrinkEntry> newEntries) {
        this.entries = (newEntries != null) ? newEntries : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drink_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvInitial;
        private final TextView tvName;
        private final TextView tvDetails;
        private final TextView tvDatetime;
        private final TextView tvUnits;

        private final SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.UK);

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial  = itemView.findViewById(R.id.tv_initial);
            tvName     = itemView.findViewById(R.id.tv_name);
            tvDetails  = itemView.findViewById(R.id.tv_details);
            tvDatetime = itemView.findViewById(R.id.tv_datetime);
            tvUnits    = itemView.findViewById(R.id.tv_units);
        }

        void bind(DrinkEntry entry) {
            String initial = (entry.name != null && !entry.name.isEmpty())
                    ? String.valueOf(entry.name.charAt(0)).toUpperCase(Locale.UK)
                    : "?";
            tvInitial.setText(initial);
            tvName.setText(entry.name);
            tvDetails.setText(String.format(Locale.UK, "%dml • %.1f%% ABV",
                    (int) entry.volumeMl, entry.abv));
            tvDatetime.setText(dateFormat.format(new Date(entry.timestamp)));
            tvUnits.setText(String.format(Locale.UK, "%.2f", entry.units));
        }
    }
}
