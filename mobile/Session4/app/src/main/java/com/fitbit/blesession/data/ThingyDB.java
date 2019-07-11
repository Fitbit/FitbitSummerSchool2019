package com.fitbit.blesession.data;

import com.fitbit.blesession.data.entities.AirQualityEntity;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AirQualityEntity.class}, version = 1, exportSchema = false)
public abstract class ThingyDB extends RoomDatabase {
    private static final String DB_NAME = "thingy.db";

    public abstract AirQualityDao airQualityDao();

    private static ThingyDB instance;

    public static void init(Context context) {
        if (instance != null) {
            return;
        }

        instance = Room.databaseBuilder(context.getApplicationContext(), ThingyDB.class, DB_NAME)
            .fallbackToDestructiveMigration()
            .build();
    }

    public static ThingyDB getDb() {
        if (instance == null) {
            throw new IllegalStateException("Database not initialized!");
        }

        return instance;
    }
}
