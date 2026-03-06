package org.me.gcu.dramwise.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DrinkTypeDao {

    @Insert
    void insert(DrinkType drinkType);

    @Insert
    void insertAll(List<DrinkType> drinkTypes);

    @Query("SELECT * FROM drink_types ORDER BY name ASC")
    LiveData<List<DrinkType>> getAll();

    @Query("SELECT COUNT(*) FROM drink_types")
    int getCount();
}