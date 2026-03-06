package org.me.gcu.dramwise.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrinkRepository {

    private static volatile DrinkRepository INSTANCE;

    private final DrinkDao drinkDao;
    private final DrinkTypeDao drinkTypeDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DrinkRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.drinkDao = db.drinkDao();
        this.drinkTypeDao = db.drinkTypeDao();
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

    // -------------------------
    // DrinkEntry methods
    // -------------------------

    public void insert(DrinkEntry entry) {
        executor.execute(() -> drinkDao.insert(entry));
    }

    public LiveData<List<DrinkEntry>> getAll() {
        return drinkDao.getAll();
    }

    public LiveData<Integer> countBetween(long startMillis, long endMillis) {
        return drinkDao.countBetween(startMillis, endMillis);
    }

    public LiveData<Double> sumUnitsBetween(long startMillis, long endMillis) {
        return drinkDao.sumUnitsBetween(startMillis, endMillis);
    }

    // -------------------------
    // DrinkType methods
    // -------------------------

    public LiveData<List<DrinkType>> getAllDrinkTypes() {
        return drinkTypeDao.getAll();
    }

    public void insertDrinkType(DrinkType drinkType) {
        executor.execute(() -> drinkTypeDao.insert(drinkType));
    }

    public void insertDrinkTypes(List<DrinkType> drinkTypes) {
        executor.execute(() -> drinkTypeDao.insertAll(drinkTypes));
    }

    public void seedDrinkTypesIfEmpty() {
        executor.execute(() -> {
            int count = drinkTypeDao.getCount();
            if (count == 0) {
                drinkTypeDao.insertAll(java.util.Arrays.asList(
                        new DrinkType("Guinness", "Beer", 568, 4.2),
                        new DrinkType("Heineken", "Beer", 568, 5.0),
                        new DrinkType("Budweiser", "Beer", 568, 5.0),
                        new DrinkType("Corona", "Beer", 355, 4.6),
                        new DrinkType("Peroni", "Beer", 330, 5.1),
                        new DrinkType("Stella Artois", "Beer", 568, 5.2),
                        new DrinkType("Carlsberg", "Beer", 568, 3.8),
                        new DrinkType("Coors Light", "Beer", 568, 4.0)
                ));
            }
        });
    }
}