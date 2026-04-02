package org.me.gcu.dramwise.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.me.gcu.dramwise.data.DrinkRepository;
import org.me.gcu.dramwise.util.DateUtil;

public class DashboardViewModel extends AndroidViewModel {

    private final LiveData<Integer> todayDrinkCount;
    private final LiveData<Double> todayUnits;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        DrinkRepository repo = DrinkRepository.getInstance(application);
        long start = DateUtil.startOfTodayMillis();
        long end = DateUtil.endOfTodayMillis();
        todayDrinkCount = repo.countBetween(start, end);
        todayUnits = repo.sumUnitsBetween(start, end);
    }

    public LiveData<Integer> getTodayDrinkCount() {
        return todayDrinkCount;
    }

    public LiveData<Double> getTodayUnits() {
        return todayUnits;
    }
}