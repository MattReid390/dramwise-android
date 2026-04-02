package org.me.gcu.dramwise.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.android.material.slider.Slider;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkRepository;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "dramwise_prefs";
    private static final String KEY_WEEKLY_LIMIT = "weekly_unit_limit";
    private static final int DEFAULT_WEEKLY_LIMIT = 14;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        Slider slider = v.findViewById(R.id.slider_weekly_limit);
        TextView tvLimit = v.findViewById(R.id.tv_weekly_limit_value);
        Button btnClear = v.findViewById(R.id.btn_clear_all_data);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Restore saved limit, falling back to the NHS default of 14 units
        int savedLimit = prefs.getInt(KEY_WEEKLY_LIMIT, DEFAULT_WEEKLY_LIMIT);
        slider.setValue(savedLimit);
        tvLimit.setText(getString(R.string.settings_weekly_limit_value, savedLimit));

        // Persist the chosen limit and update the label whenever the slider moves
        slider.addOnChangeListener((s, value, fromUser) -> {
            int limit = (int) value;
            tvLimit.setText(getString(R.string.settings_weekly_limit_value, limit));
            prefs.edit().putInt(KEY_WEEKLY_LIMIT, limit).apply();
        });

        // Clear all data with confirmation dialog
        btnClear.setOnClickListener(view ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.settings_clear_data_title))
                        .setMessage(getString(R.string.settings_clear_data_message))
                        .setPositiveButton(getString(R.string.settings_clear_data_confirm), (dialog, which) -> {
                            DrinkRepository.getInstance(requireContext()).clearHistory();
                            Toast.makeText(requireContext(), getString(R.string.settings_cleared_toast), Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(getString(R.string.settings_clear_data_cancel), null)
                        .show()
        );

        return v;
    }
}