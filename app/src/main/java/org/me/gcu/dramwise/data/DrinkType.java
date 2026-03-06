package org.me.gcu.dramwise.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drink_types")
public class DrinkType {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String category;
    public double defaultVolumeMl;
    public double abv;

    public DrinkType(String name, String category, double defaultVolumeMl, double abv) {
        this.name = name;
        this.category = category;
        this.defaultVolumeMl = defaultVolumeMl;
        this.abv = abv;
    }
}