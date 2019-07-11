package com.fitbit.blesession.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AirQuality")
public class AirQualityEntity {

    public AirQualityEntity(long timestamp, String deviceId, int co2, int voc) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.co2 = co2;
        this.voc = voc;
    }

    @PrimaryKey(autoGenerate = true)
    public
    int id;

    @ColumnInfo(name = "timestamp")
    public
    long timestamp;
    @ColumnInfo(name = "deviceId")
    String deviceId;
    @ColumnInfo(name = "CO2")
    int co2;
    @ColumnInfo(name = "VOC")
    int voc;

    public String getDeviceId() {
        return deviceId;
    }

    public int getCo2() {
        return co2;
    }

    public int getVoc() {
        return voc;
    }
}
