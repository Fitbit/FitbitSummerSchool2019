package com.fitbit.blesession;

import com.fitbit.blesession.data.ThingyDB;
import com.fitbit.blesession.data.entities.AirQualityEntity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView entryList;
    private AirQualityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        entryList = findViewById(R.id.list);

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);

        adapter = new AirQualityAdapter(this,
            new ArrayList<AirQualityEntity>(),
            prefs.getInt(SettingsActivity.K_CO2_THRESHOLD, 0),
            prefs.getInt(SettingsActivity.K_VOC_THRESHOLD, 0)
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
            layoutManager.getOrientation());

        entryList.setAdapter(adapter);
        entryList.setLayoutManager(layoutManager);
        entryList.addItemDecoration(dividerItemDecoration);

        loadFromDb();

    }

    private void loadFromDb() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<AirQualityEntity> entities = ThingyDB.getDb()
                    .airQualityDao()
                    .getAllAirQualityEntries();
                Log.d("AIRQUALITY: ", "get from db  " + entities.size() + " records");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setEntities(entities);
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
