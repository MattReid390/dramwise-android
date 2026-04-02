package org.me.gcu.dramwise.ui.history;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkEntry;
import org.me.gcu.dramwise.data.DrinkRepository;
import org.me.gcu.dramwise.util.DateUtil;

import java.util.List;

/**
 * Fragment that displays a simple text-based history of all drink entries.
 * Also provides an option to clear all saved history with confirmation.
 */
public class HistoryFragment extends Fragment {

    private TextView tvHistory;
    private Button btnClearHistory;
    private DrinkRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        // UI references
        tvHistory = v.findViewById(R.id.tv_history);
        btnClearHistory = v.findViewById(R.id.btn_clear_history);

        // Repository for accessing Room database
        repository = DrinkRepository.getInstance(requireContext());

        // Observe all drink entries and update the UI whenever data changes
        repository.getAll().observe(getViewLifecycleOwner(), this::render);

        // Clear history button with confirmation dialog
        btnClearHistory.setOnClickListener(view ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Clear History")
                        .setMessage("Are you sure you want to delete all saved drink entries?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Clear all entries and notify user
                            repository.clearHistory();
                            Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        return v;
    }

    /**
     * Renders the list of drink entries into a readable text format.
     * If no entries exist, displays a placeholder message.
     */
    private void render(List<DrinkEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            tvHistory.setText("No drinks logged yet.\nStart tracking to see your history..");
            return;
        }

        // Build a readable history list
        StringBuilder sb = new StringBuilder();
        for (DrinkEntry e : entries) {
            sb.append(DateUtil.formatDateTime(e.timestamp))   // formatted date/time
                    .append(" — ")
                    .append(e.name)                           // drink name
                    .append(" (")
                    .append((int) e.volumeMl).append("ml, ")  // volume
                    .append(e.abv).append("%)")               // ABV
                    .append(" = ")
                    .append(String.format(java.util.Locale.UK, "%.2f", e.units)) // units
                    .append(" units")
                    .append("\n");
        }

        // Display the formatted history
        tvHistory.setText(sb.toString().trim());
    }
}