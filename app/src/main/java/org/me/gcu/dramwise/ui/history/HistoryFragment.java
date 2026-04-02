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
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkEntry;
import org.me.gcu.dramwise.util.DateUtil;

import java.util.List;

/**
 * Fragment that displays a simple text-based history of all drink entries.
 * Also provides an option to clear all saved history with confirmation.
 */
public class HistoryFragment extends Fragment {

    private TextView tvHistory;
    private Button btnClearHistory;

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

        // FIXED: Obtain ViewModel instead of accessing the repository directly.
        // The ViewModel survives configuration changes (e.g. screen rotation)
        // so LiveData is not re-subscribed unnecessarily.
        HistoryViewModel viewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        // Observe all drink entries and update the UI whenever data changes
        viewModel.getAllEntries().observe(getViewLifecycleOwner(), this::render); // FIXED

        // Clear history button with confirmation dialog
        btnClearHistory.setOnClickListener(view ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.history_clear_title))
                        .setMessage(getString(R.string.history_clear_message))
                        .setPositiveButton(getString(R.string.history_clear_confirm), (dialog, which) -> {
                            viewModel.clearHistory(); // FIXED
                            Toast.makeText(requireContext(), getString(R.string.history_cleared_toast), Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(getString(R.string.history_clear_cancel), null)
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
            tvHistory.setText(getString(R.string.history_empty));
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