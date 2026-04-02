package org.me.gcu.dramwise.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {DrinkEntry.class, DrinkType.class}, version = 2, exportSchema = true) // FIXED
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // FIXED: Proper migration from version 1 to version 2.
    // Version 2 introduced the drink_types table.
    // SQL is taken directly from the Room-generated AppDatabase_Impl to ensure
    // column names, types and constraints match the entity exactly.
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `drink_types` " +
                            "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT, " +
                            "`category` TEXT, " +
                            "`defaultVolumeMl` REAL NOT NULL, " +
                            "`abv` REAL NOT NULL)"
            );
        }
    };

    public abstract DrinkDao drinkDao();
    public abstract DrinkTypeDao drinkTypeDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "dramwise_db"
                            )
                            .addMigrations(MIGRATION_1_2) // FIXED
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}