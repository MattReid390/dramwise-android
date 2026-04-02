package org.me.gcu.dramwise.ui.insights;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
 * Shows a bar chart of units per day, a weekly summary,
 * and rule-based smart feedback.
 */
public class InsightsFragment extends Fragment {

    private BarChart barChart;
    private TextView textWeeklySummary;
    private TextView textRiskBadge;
    private TextView textSmartFeedback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_insights, container, false);

        barChart = v.findViewById(R.id.barChartWeeklyUnits);
        textWeeklySummary = v.findViewById(R.id.textWeeklySummary);
        textRiskBadge = v.findViewById(R.id.textRiskBadge);
        textSmartFeedback = v.findViewById(R.id.textSmartFeedback);

        setupChartAppearance();

        // FIXED: Obtain ViewModel instead of accessing the repository directly.
        // The ViewModel survives configuration changes (e.g. screen rotation)
        // so LiveData is not re-subscribed unnecessarily.
        InsightsViewModel viewModel =
                new ViewModelProvider(this).get(InsightsViewModel.class);

        viewModel.getWeeklyUnits().observe(getViewLifecycleOwner(), this::updateWeeklyChart); // FIXED

        return v;
    }

    /**
     * Configures the static appearance and behaviour of the bar chart.
     */
    private void setupChartAppearance() {
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    /**
     * Updates the bar chart, summary text, and smart feedback
     * using the last 7 days of data.
     */
    private void updateWeeklyChart(List<DailyUnits> databaseResults) {

        Map<String, Float> unitsByDay = new HashMap<>();

        if (databaseResults != null) {
            for (DailyUnits item : databaseResults) {
                unitsByDay.put(item.day, item.totalUnits);
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat labelFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -6);

        float weeklyTotal = 0f;
        float maxDay = 0f;
        float weekendTotal = 0f;
        float weekdayTotal = 0f;
        String highestDayLabel = "";

        for (int i = 0; i < 7; i++) {
            String dbKey = dbDateFormat.format(calendar.getTime());
            String label = labelFormat.format(calendar.getTime());

            float totalUnits = unitsByDay.getOrDefault(dbKey, 0f);

            entries.add(new BarEntry(i, totalUnits));
            labels.add(label);

            weeklyTotal += totalUnits;

            if (totalUnits > maxDay) {
                maxDay = totalUnits;
                highestDayLabel = label;
            }

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                weekendTotal += totalUnits;
            } else {
                weekdayTotal += totalUnits;
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Units per Day");
        dataSet.setValueTextSize(12f);
        dataSet.setColor(Color.parseColor("#4CAF50"));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barData.setValueFormatter(new ValueFormatter() {
            private final DecimalFormat format = new DecimalFormat("0.0");

            @Override
            public String getBarLabel(BarEntry barEntry) {
                if (barEntry.getY() == 0f) {
                    return "0";
                }
                return format.format(barEntry.getY());
            }
        });

        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(6.5f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMaximum(Math.max(10f, maxDay + 2f));

        barChart.animateY(800);
        barChart.invalidate();

        DecimalFormat summaryFormat = new DecimalFormat("0.0");
        String summary = getString(R.string.insights_summary,
                summaryFormat.format(weeklyTotal),
                summaryFormat.format(weeklyTotal / 7f));
        textWeeklySummary.setText(summary);

        updateRiskBadge(weeklyTotal);

        textSmartFeedback.setText(
                generateSmartFeedback(weeklyTotal, maxDay, weekendTotal, weekdayTotal, highestDayLabel)
        );
    }

    private void updateRiskBadge(float weeklyTotal) {
        if (weeklyTotal == 0f) {
            textRiskBadge.setText(getString(R.string.risk_no_data));
            textRiskBadge.setBackgroundResource(R.drawable.bg_risk_badge_low);
        } else if (weeklyTotal > 14f) {
            textRiskBadge.setText(getString(R.string.risk_high));
            textRiskBadge.setBackgroundResource(R.drawable.bg_risk_badge_high);
        } else if (weeklyTotal >= 10f) {
            textRiskBadge.setText(getString(R.string.risk_moderate));
            textRiskBadge.setBackgroundResource(R.drawable.bg_risk_badge_moderate);
        } else {
            textRiskBadge.setText(getString(R.string.risk_low));
            textRiskBadge.setBackgroundResource(R.drawable.bg_risk_badge_low);
        }
    }

    /**
     * Generates rule-based personalised feedback from weekly drinking patterns.
     */
    private String generateSmartFeedback(float weeklyTotal, float maxDay, float weekendTotal, float weekdayTotal, String highestDayLabel) {
        StringBuilder feedback = new StringBuilder();

        if (weeklyTotal == 0f) {
            return getString(R.string.insights_no_data_feedback);
        }

        if (weeklyTotal > 14f) {
            feedback.append("You are above the recommended weekly guideline. ");
        } else {
            feedback.append("Your drinking this week is within the recommended weekly guideline. ");
        }

        if (maxDay >= 6f && !highestDayLabel.isEmpty()) {
            feedback.append("Your highest intake day was ")
                    .append(highestDayLabel)
                    .append(", which may increase short-term health risk. ");
        }

        if (weekendTotal > weekdayTotal && weekendTotal >= (weeklyTotal * 0.6f)) {
            feedback.append("Most of your drinking is concentrated at the weekend. ");
        }

        if (weeklyTotal >= 20f) {
            feedback.append("This pattern may indicate elevated alcohol-related risk.");
        } else if (weeklyTotal >= 10f) {
            feedback.append("Your current pattern suggests moderate alcohol consumption.");
        } else {
            feedback.append("Your current pattern suggests relatively low alcohol consumption.");
        }

        return "• " + feedback.toString().trim().replace(". ", ".\n• ");
    }
}