package org.me.gcu.dramwise.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrinkRepository {

    private static volatile DrinkRepository INSTANCE;

    private final DrinkDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DrinkRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.dao = db.drinkDao();
    }

    public static DrinkRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DrinkRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DrinkRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public void insert(DrinkEntry entry) {
        executor.execute(() -> dao.insert(entry));
    }

    public LiveData<List<DrinkEntry>> getAll() {
        return dao.getAll();
    }

    public LiveData<Integer> countBetween(long startMillis, long endMillis) {
        return dao.countBetween(startMillis, endMillis);
    }

    public LiveData<Double> sumUnitsBetween(long startMillis, long endMillis) {
        return dao.sumUnitsBetween(startMillis, endMillis);
    }
}