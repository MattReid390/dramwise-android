package org.me.gcu.dramwise.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DrinkDao {

    @Insert
    long insert(DrinkEntry entry);

    @Query("SELECT * FROM drink_entries ORDER BY timestamp DESC")
    LiveData<List<DrinkEntry>> getAll();

    @Query("SELECT COUNT(*) FROM drink_entries WHERE timestamp BETWEEN :startMillis AND :endMillis")
    LiveData<Integer> countBetween(long startMillis, long endMillis);

    @Query("SELECT COALESCE(SUM(units), 0) FROM drink_entries WHERE timestamp BETWEEN :startMillis AND :endMillis")
    LiveData<Double> sumUnitsBetween(long startMillis, long endMillis);

    @Query("DELETE FROM drink_entries")
    void deleteAll();
}