package org.me.gcu.dramwise.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drink_entries")
public class DrinkEntry {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public double volumeMl;
    public double abv;
    public double units;
    public long timestamp;

    public DrinkEntry(String name, double volumeMl, double abv, double units, long timestamp) {
        this.name = name;
        this.volumeMl = volumeMl;
        this.abv = abv;
        this.units = units;
        this.timestamp = timestamp;
    }
}