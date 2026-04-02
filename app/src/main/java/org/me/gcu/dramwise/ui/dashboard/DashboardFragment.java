package org.me.gcu.dramwise.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.dramwise.R;

public class DashboardFragment extends Fragment {

    private TextView tvTodayDrinks;
    private TextView tvTodayUnits;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTodayDrinks = v.findViewById(R.id.tv_today_drinks);
        tvTodayUnits = v.findViewById(R.id.tv_today_units);

        // FIXED: Obtain ViewModel instead of accessing the repository directly.
        // The ViewModel survives configuration changes (e.g. screen rotation)
        // so LiveData is not re-subscribed unnecessarily.
        DashboardViewModel viewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.getTodayDrinkCount().observe(getViewLifecycleOwner(), count -> {
            int safeCount = (count == null) ? 0 : count;
            tvTodayDrinks.setText(getString(R.string.today_drinks, safeCount));
        });

        viewModel.getTodayUnits().observe(getViewLifecycleOwner(), sum -> {
            double safeSum = (sum == null) ? 0.0 : sum;
            tvTodayUnits.setText(getString(R.string.today_units, safeSum));
        });

        return v;
    }
}