package org.me.gcu.dramwise.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.me.gcu.dramwise.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private static final String PREFS_NAME = "dramwise_prefs";
    private static final String KEY_WEEKLY_LIMIT = "weekly_unit_limit";
    private static final int DEFAULT_WEEKLY_LIMIT = 14;

    private TextView tvTodayDrinks;
    private TextView tvTodayUnits;
    private TextView tvDate;
    private TextView tvWeeklyUnits;
    private LinearProgressIndicator progressWeekly;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTodayDrinks = v.findViewById(R.id.tv_today_drinks);
        tvTodayUnits = v.findViewById(R.id.tv_today_units);
        tvDate = v.findViewById(R.id.tv_date);
        tvWeeklyUnits = v.findViewById(R.id.tv_weekly_units);
        progressWeekly = v.findViewById(R.id.progress_weekly);

        // Show current date below the "Today" heading
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.UK);
        tvDate.setText(dateFormat.format(new Date()));

        // Read the user's weekly limit from Settings (defaults to 14)
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int weeklyLimit = prefs.getInt(KEY_WEEKLY_LIMIT, DEFAULT_WEEKLY_LIMIT);

        DashboardViewModel viewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.getTodayDrinkCount().observe(getViewLifecycleOwner(), count -> {
            int safeCount = (count == null) ? 0 : count;
            tvTodayDrinks.setText(String.valueOf(safeCount));
        });

        viewModel.getTodayUnits().observe(getViewLifecycleOwner(), sum -> {
            double safeSum = (sum == null) ? 0.0 : sum;
            tvTodayUnits.setText(String.format(Locale.UK, "%.1f", safeSum));
        });

        viewModel.getWeeklyUnits().observe(getViewLifecycleOwner(), weeklySum -> {
            double safeSum = (weeklySum == null) ? 0.0 : weeklySum;
            tvWeeklyUnits.setText(getString(R.string.dashboard_weekly_progress, safeSum, weeklyLimit));
            int progress = (int) Math.min((safeSum / weeklyLimit) * 100, 100);
            progressWeekly.setProgressCompat(progress, true);
        });

        return v;
    }
}
