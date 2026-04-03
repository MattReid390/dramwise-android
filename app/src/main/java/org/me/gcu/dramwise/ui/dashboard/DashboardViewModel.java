package org.me.gcu.dramwise.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.me.gcu.dramwise.data.DrinkRepository;
import org.me.gcu.dramwise.util.DateUtil;

import java.util.Calendar;

public class DashboardViewModel extends AndroidViewModel {

    private final LiveData<Integer> todayDrinkCount;
    private final LiveData<Double> todayUnits;
    private final LiveData<Double> weeklyUnits;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        DrinkRepository repo = DrinkRepository.getInstance(application);
        long start = DateUtil.startOfTodayMillis();
        long end = DateUtil.endOfTodayMillis();
        todayDrinkCount = repo.countBetween(start, end);
        todayUnits = repo.sumUnitsBetween(start, end);

        // Start of current week — roll back to Monday 00:00:00
        Calendar weekCal = Calendar.getInstance();
        int dow = weekCal.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dow == Calendar.SUNDAY) ? 6 : dow - Calendar.MONDAY;
        weekCal.add(Calendar.DAY_OF_YEAR, -daysFromMonday);
        weekCal.set(Calendar.HOUR_OF_DAY, 0);
        weekCal.set(Calendar.MINUTE, 0);
        weekCal.set(Calendar.SECOND, 0);
        weekCal.set(Calendar.MILLISECOND, 0);
        weeklyUnits = repo.sumUnitsBetween(weekCal.getTimeInMillis(), end);
    }

    public LiveData<Integer> getTodayDrinkCount() {
        return todayDrinkCount;
    }

    public LiveData<Double> getTodayUnits() {
        return todayUnits;
    }

    public LiveData<Double> getWeeklyUnits() {
        return weeklyUnits;
    }
}
