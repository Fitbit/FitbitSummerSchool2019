package com.fitbit.blesession.bluetooth;

import com.fitbit.blesession.data.ThingyDB;
import com.fitbit.blesession.data.entities.AirQualityEntity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.thingylib.ThingyListener;
import no.nordicsemi.android.thingylib.ThingySdkManager;

public class ButtonThingyListener implements ThingyListener {
    private static final String TAG = ButtonThingyListener.class.getName();

    private ThingySdkManager thingySdkManager;
    private Context context;
    private List<AirQualityEntity> records = new ArrayList<>();

    private Handler writeHandler;
    private long batchSaveInterval = 60 * 1000;

    public ButtonThingyListener(ThingySdkManager thingySdkManager, Context context) {
        this.thingySdkManager = thingySdkManager;
        this.context = context;

        HandlerThread thread = new HandlerThread("I handle things");
        thread.start();
        writeHandler = new Handler(thread.getLooper());

        writeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ThingyDB.getDb()
                        .airQualityDao()
                        .saveAirQualityEntries(records);

                    records.clear();
                } finally {
                    writeHandler.postDelayed(this, batchSaveInterval);
                }
            }
        }, batchSaveInterval);
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device, int connectionState) {
        Log.d(TAG, "Connected to " + device);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, int connectionState) {
        Log.d(TAG, "" + device + " disconnected.");
    }

    @Override
    public void onServiceDiscoveryCompleted(BluetoothDevice device) {
        thingySdkManager.enableButtonStateNotification(device, true);
    }

    @Override
    public void onBatteryLevelChanged(BluetoothDevice bluetoothDevice, int batteryLevel) {

    }

    @Override
    public void onTemperatureValueChangedEvent(BluetoothDevice bluetoothDevice, String temperature) {

    }

    @Override
    public void onPressureValueChangedEvent(BluetoothDevice bluetoothDevice, String pressure) {

    }

    @Override
    public void onHumidityValueChangedEvent(BluetoothDevice bluetoothDevice, String humidity) {

    }

    @Override
    public void onAirQualityValueChangedEvent(BluetoothDevice bluetoothDevice, int eco2, int tvoc) {
        records.add(new AirQualityEntity(
            System.currentTimeMillis(),
            bluetoothDevice.getAddress(),
            eco2,
            tvoc
        ));
    }

    @Override
    public void onColorIntensityValueChangedEvent(BluetoothDevice bluetoothDevice, float red, float green, float blue, float alpha) {

    }

    @Override
    public void onButtonStateChangedEvent(BluetoothDevice bluetoothDevice, int buttonState) {
        Toast.makeText(context, "Button state: " + buttonState, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTapValueChangedEvent(BluetoothDevice bluetoothDevice, int direction, int count) {

    }

    @Override
    public void onOrientationValueChangedEvent(BluetoothDevice bluetoothDevice, int orientation) {

    }

    @Override
    public void onQuaternionValueChangedEvent(BluetoothDevice bluetoothDevice, float w, float x, float y, float z) {

    }

    @Override
    public void onPedometerValueChangedEvent(BluetoothDevice bluetoothDevice, int steps, long duration) {

    }

    @Override
    public void onAccelerometerValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

    }

    @Override
    public void onGyroscopeValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

    }

    @Override
    public void onCompassValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

    }

    @Override
    public void onEulerAngleChangedEvent(BluetoothDevice bluetoothDevice, float roll, float pitch, float yaw) {

    }

    @Override
    public void onRotationMatrixValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] matrix) {

    }

    @Override
    public void onHeadingValueChangedEvent(BluetoothDevice bluetoothDevice, float heading) {

    }

    @Override
    public void onGravityVectorChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

    }

    @Override
    public void onSpeakerStatusValueChangedEvent(BluetoothDevice bluetoothDevice, int status) {

    }

    @Override
    public void onMicrophoneValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] data) {

    }



}
