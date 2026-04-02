package org.me.gcu.dramwise.ui.insights;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.me.gcu.dramwise.data.DailyUnits;
import org.me.gcu.dramwise.data.DrinkRepository;

import java.util.List;

public class InsightsViewModel extends AndroidViewModel {

    private final LiveData<List<DailyUnits>> weeklyUnits;

    public InsightsViewModel(@NonNull Application application) {
        super(application);
        weeklyUnits = DrinkRepository.getInstance(application).getUnitsLast7Days();
    }

    public LiveData<List<DailyUnits>> getWeeklyUnits() {
        return weeklyUnits;
    }
}