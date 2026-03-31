package org.me.gcu.dramwise.ui.insights;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DailyUnits;
import org.me.gcu.dramwise.data.DrinkRepository;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment responsible for displaying weekly drinking insights.
 * Shows a bar chart of units per day and a summary of weekly totals.
 */
public class InsightsFragment extends Fragment {

    private BarChart barChart;
    private TextView textWeeklySummary;
    private DrinkRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate layout for this fragment
        View v = inflater.inflate(R.layout.fragment_insights, container, false);

        // UI references
        barChart = v.findViewById(R.id.barChartWeeklyUnits);
        textWeeklySummary = v.findViewById(R.id.textWeeklySummary);

        // Repository for accessing Room data
        repository = DrinkRepository.getInstance(requireContext());

        // Configure chart styling and behaviour
        setupChartAppearance();

        // Observe LiveData for last 7 days of units and update chart when data changes
        repository.getUnitsLast7Days()
                .observe(getViewLifecycleOwner(), this::updateWeeklyChart);

        return v;
    }

    /**
     * Configures the static appearance and behaviour of the bar chart.
     * This includes axis settings, legend visibility, and interaction rules.
     */
    private void setupChartAppearance() {
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);

        // Remove default description text
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        // Enable legend (shows dataset label)
        Legend legend = barChart.getLegend();
        legend.setEnabled(true);

        // Configure X-axis (bottom labels)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Configure left Y-axis (units)
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        // Disable right Y-axis for cleaner look
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    /**
     * Updates the bar chart and summary text using the last 7 days of data.
     * Converts database date strings into chart labels and calculates totals.
     */
    private void updateWeeklyChart(List<DailyUnits> databaseResults) {

        // Map of dateString -> units for quick lookup
        Map<String, Float> unitsByDay = new HashMap<>();

        if (databaseResults != null) {
            for (DailyUnits item : databaseResults) {
                unitsByDay.put(item.day, item.totalUnits);
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Format used by SQLite query (yyyy-MM-dd)
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Format for chart labels (Mon, Tue, etc.)
        SimpleDateFormat labelFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        // Start at midnight 6 days ago (covers 7 days including today)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -6);

        float weeklyTotal = 0f;
        float maxDay = 0f;

        // Build chart entries for each of the last 7 days
        for (int i = 0; i < 7; i++) {
            String dbKey = dbDateFormat.format(calendar.getTime());
            String label = labelFormat.format(calendar.getTime());

            // Units for this day (0 if no entry)
            float totalUnits = unitsByDay.getOrDefault(dbKey, 0f);

            entries.add(new BarEntry(i, totalUnits));
            labels.add(label);

            weeklyTotal += totalUnits;
            maxDay = Math.max(maxDay, totalUnits);

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Create dataset for the chart
        BarDataSet dataSet = new BarDataSet(entries, "Units per Day");
        dataSet.setValueTextSize(12f);

        // Format bar values to 1 decimal place
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barData.setValueFormatter(new ValueFormatter() {
            private final DecimalFormat format = new DecimalFormat("0.0");

            @Override
            public String getBarLabel(BarEntry barEntry) {
                return format.format(barEntry.getY());
            }
        });

        barChart.setData(barData);

        // Apply X-axis labels (Mon, Tue, etc.)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(6.5f);

        // Adjust Y-axis max to fit the highest bar comfortably
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMaximum(Math.max(5f, maxDay + 2f));

        // Animate and refresh chart
        barChart.animateY(800);
        barChart.invalidate();

        // Build weekly summary text
        DecimalFormat summaryFormat = new DecimalFormat("0.0");
        String summary = "Total this week: " + summaryFormat.format(weeklyTotal)
                + " units\nAverage per day: " + summaryFormat.format(weeklyTotal / 7f) + " units";

        // Compare against UK recommended guideline (14 units/week)
        if (weeklyTotal > 14f) {
            summary += "\nYou are above the recommended weekly guideline.";
        } else {
            summary += "\nYou are within the recommended weekly guideline.";
        }

        textWeeklySummary.setText(summary);
    }
}