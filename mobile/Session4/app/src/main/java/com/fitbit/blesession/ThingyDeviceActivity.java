package com.fitbit.blesession;

import androidx.appcompat.app.AppCompatActivity;
import no.nordicsemi.android.thingylib.ThingyListener;
import no.nordicsemi.android.thingylib.ThingyListenerHelper;
import no.nordicsemi.android.thingylib.ThingySdkManager;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fitbit.blesession.bluetooth.ButtonThingyListener;
import com.fitbit.blesession.bluetooth.MyThingyService;

public class ThingyDeviceActivity extends AppCompatActivity implements  ThingySdkManager.ServiceConnectionListener {
    private static final String TAG = ThingyDeviceActivity.class.getName();
    private ThingySdkManager thingySdkManager;
    private BluetoothDevice bluetoothDevice;
    private ThingyListener thingyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thingy_device);

        Intent intent = getIntent();
        bluetoothDevice = intent.getParcelableExtra(MainActivity.BLUETOOTH_DEVICE_KEY);

        thingySdkManager = ThingySdkManager.getInstance();
        Log.d(TAG, "Got this in extras: " + bluetoothDevice);

        thingySdkManager.enableAirQualityNotifications(bluetoothDevice, true);
        thingyListener = new ButtonThingyListener(thingySdkManager, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        thingySdkManager.bindService(this, MyThingyService.class);
        ThingyListenerHelper.registerThingyListener(this, thingyListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        thingySdkManager.unbindService(this);
        ThingyListenerHelper.unregisterThingyListener(this, thingyListener);
    }

    @Override
    public void onServiceConnected() {
        Log.d(TAG, "Thingy Service Connected");
        thingySdkManager.connectToThingy(this, bluetoothDevice, MyThingyService.class);
    }
}
