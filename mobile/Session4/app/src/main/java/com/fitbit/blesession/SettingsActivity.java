package com.fitbit.blesession;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "SETTINGS";

    public static final String K_CO2_THRESHOLD = "co2_threshold";
    public static final String K_VOC_THRESHOLD = "voc_threshold";


    private EditText co2Threshold;
    private EditText vocThreshold;

    private Button saveButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        co2Threshold = findViewById(R.id.pollution_threshold_co2_input);
        vocThreshold = findViewById(R.id.pollution_threshold_voc_input);
        saveButton = findViewById(R.id.button_save);

        setupButton();
        populateFields();
    }

    private void populateFields() {
        co2Threshold.setText(String.valueOf(sharedPreferences.getInt(K_CO2_THRESHOLD, 50)));
        vocThreshold.setText(String.valueOf(sharedPreferences.getInt(K_VOC_THRESHOLD, 50)));

    }

    private void setupButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit()
                    .putInt(K_CO2_THRESHOLD, Integer.parseInt(co2Threshold.getText().toString()))
                    .putInt(K_VOC_THRESHOLD, Integer.parseInt(vocThreshold.getText().toString()))
                    .apply();

                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
