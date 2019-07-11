package com.fitbit.blesession.data;

import com.fitbit.blesession.data.entities.AirQualityEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AirQualityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAirQualityEntries(List<AirQualityEntity> entities);

    @Query("SELECT* FROM AirQuality")
    List<AirQualityEntity> getAllAirQualityEntries();
}
