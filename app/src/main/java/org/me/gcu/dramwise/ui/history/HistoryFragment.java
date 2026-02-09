package org.me.gcu.dramwise.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkEntry;
import org.me.gcu.dramwise.data.DrinkRepository;
import org.me.gcu.dramwise.util.DateUtil;

import java.util.List;

public class HistoryFragment extends Fragment {

    private TextView tvHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        tvHistory = v.findViewById(R.id.tv_history);

        DrinkRepository.getInstance(requireContext()).getAll()
                .observe(getViewLifecycleOwner(), this::render);

        return v;
    }

    private void render(List<DrinkEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            tvHistory.setText("No entries yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (DrinkEntry e : entries) {
            sb.append(DateUtil.formatDateTime(e.timestamp))
                    .append(" — ")
                    .append(e.name)
                    .append(" (")
                    .append((int) e.volumeMl).append("ml, ")
                    .append(e.abv).append("%)")
                    .append(" = ")
                    .append(String.format(java.util.Locale.UK, "%.2f", e.units))
                    .append(" units")
                    .append("\n");
        }

        tvHistory.setText(sb.toString().trim());
    }
}