package org.me.gcu.dramwise.ui.history;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkEntry;

import java.util.List;

/**
 * Fragment that displays a card-based history of all drink entries.
 * Also provides an option to clear all saved history with confirmation.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private LinearLayout layoutEmpty;
    private TextView tvEntryCount;
    private Button btnClearHistory;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory       = v.findViewById(R.id.rv_history);
        layoutEmpty     = v.findViewById(R.id.layout_empty);
        tvEntryCount    = v.findViewById(R.id.tv_entry_count);
        btnClearHistory = v.findViewById(R.id.btn_clear_history);

        // Set up RecyclerView with the card adapter
        adapter = new HistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(adapter);

        HistoryViewModel viewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        viewModel.getAllEntries().observe(getViewLifecycleOwner(), this::render);

        btnClearHistory.setOnClickListener(view ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.history_clear_title))
                        .setMessage(getString(R.string.history_clear_message))
                        .setPositiveButton(getString(R.string.history_clear_confirm), (dialog, which) -> {
                            viewModel.clearHistory();
                            Toast.makeText(requireContext(),
                                    getString(R.string.history_cleared_toast),
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(getString(R.string.history_clear_cancel), null)
                        .show()
        );

        return v;
    }

    private void render(List<DrinkEntry> entries) {
        boolean isEmpty = (entries == null || entries.isEmpty());

        rvHistory.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        if (!isEmpty) {
            adapter.updateData(entries);
            int count = entries.size();
            tvEntryCount.setText(count == 1 ? "1 entry" : count + " entries");
        } else {
            tvEntryCount.setText("");
        }
    }
}
