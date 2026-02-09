package org.me.gcu.dramwise.ui.insights;

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

import java.util.List;

public class InsightsFragment extends Fragment {

    private TextView tvInsights;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_insights, container, false);
        tvInsights = v.findViewById(R.id.tv_insights);

        DrinkRepository.getInstance(requireContext()).getAll()
                .observe(getViewLifecycleOwner(), this::renderInsights);

        return v;
    }

    private void renderInsights(List<DrinkEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            tvInsights.setText("No data yet. Add drinks to see insights.");
            return;
        }

        long now = System.currentTimeMillis();
        long sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000);

        double totalUnits7d = 0.0;
        int count7d = 0;

        for (DrinkEntry e : entries) {
            if (e.timestamp >= sevenDaysAgo) {
                totalUnits7d += e.units;
                count7d++;
            }
        }

        String text = "Last 7 days:\n"
                + "Drinks: " + count7d + "\n"
                + "Units: " + String.format(java.util.Locale.UK, "%.2f", totalUnits7d);

        tvInsights.setText(text);
    }
}