package org.me.gcu.dramwise.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository layer for managing drink-related data operations.
 *
 * Acts as the single source of truth for ViewModels by:
 *  - Exposing LiveData from DAOs
 *  - Running write operations off the main thread
 *  - Coordinating access to DrinkDao and DrinkTypeDao
 *
 * Implements the Singleton pattern to ensure one shared instance.
 */
public class DrinkRepository {

    // Singleton instance (volatile ensures visibility across threads)
    private static volatile DrinkRepository INSTANCE;

    // DAOs for accessing drink entries and drink types
    private final DrinkDao drinkDao;
    private final DrinkTypeDao drinkTypeDao;

    // Executor for background database writes (avoids blocking UI thread)
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Private constructor to enforce Singleton usage.
     * Initializes the Room database and retrieves DAOs.
     */
    private DrinkRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.drinkDao = db.drinkDao();
        this.drinkTypeDao = db.drinkTypeDao();
    }

    /**
     * Returns the singleton instance of the repository.
     * Uses double-checked locking for thread-safe lazy initialization.
     */
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

    // ---------------------------------------------------------
    // DrinkEntry methods
    // ---------------------------------------------------------

    /**
     * Inserts a DrinkEntry on a background thread.
     * Room requires write operations to be off the main thread.
     */
    public void insert(DrinkEntry entry) {
        executor.execute(() -> {
            try {                                                             // FIXED
                drinkDao.insert(entry);
            } catch (Exception e) {
                android.util.Log.e("DrinkRepository", "Failed to insert drink entry", e);
            }
        });
    }

    /**
     * Returns all drink entries as LiveData.
     * UI automatically updates when database changes.
     */
    public LiveData<List<DrinkEntry>> getAll() {
        return drinkDao.getAll();
    }

    /**
     * Counts entries within a given time range.
     */
    public LiveData<Integer> countBetween(long startMillis, long endMillis) {
        return drinkDao.countBetween(startMillis, endMillis);
    }

    /**
     * Sums alcohol units within a given time range.
     */
    public LiveData<Double> sumUnitsBetween(long startMillis, long endMillis) {
        return drinkDao.sumUnitsBetween(startMillis, endMillis);
    }

    /**
     * Returns daily unit totals for the last 7 days.
     * Calculates midnight 6 days ago to define the range.
     */
    public LiveData<List<DailyUnits>> getUnitsLast7Days() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        // Normalize to start of today (00:00:00.000)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);

        // Go back 6 days to include today + previous 6 days = 7 total
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -6);

        long startTime = calendar.getTimeInMillis();
        return drinkDao.getUnitsLast7Days(startTime);
    }

    /**
     * Deletes all saved drink history on a background thread.
     */
    public void clearHistory() {
        executor.execute(() -> {
            try {                                                             // FIXED
                drinkDao.deleteAll();
            } catch (Exception e) {
                android.util.Log.e("DrinkRepository", "Failed to clear history", e);
            }
        });
    }

    // ---------------------------------------------------------
    // DrinkType methods
    // ---------------------------------------------------------

    /**
     * Returns all drink types (e.g., Guinness, Heineken, etc.).
     */
    public LiveData<List<DrinkType>> getAllDrinkTypes() {
        return drinkTypeDao.getAll();
    }

    /**
     * Inserts a single DrinkType on a background thread.
     */
    public void insertDrinkType(DrinkType drinkType) {
        executor.execute(() -> {
            try {                                                             // FIXED
                drinkTypeDao.insert(drinkType);
            } catch (Exception e) {
                android.util.Log.e("DrinkRepository", "Failed to insert drink type", e);
            }
        });
    }

    /**
     * Inserts multiple DrinkTypes on a background thread.
     */
    public void insertDrinkTypes(List<DrinkType> drinkTypes) {
        executor.execute(() -> {
            try {                                                             // FIXED
                drinkTypeDao.insertAll(drinkTypes);
            } catch (Exception e) {
                android.util.Log.e("DrinkRepository", "Failed to insert drink types", e);
            }
        });
    }

    /**
     * Seeds the database with default drink types if none exist.
     * Runs once on first launch to populate the app with common beers.
     */
    public void seedDrinkTypesIfEmpty() {
        executor.execute(() -> {
            try {                                                             // FIXED
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
            } catch (Exception e) {
                android.util.Log.e("DrinkRepository", "Failed to seed drink types", e);
            }
        });
    }
}