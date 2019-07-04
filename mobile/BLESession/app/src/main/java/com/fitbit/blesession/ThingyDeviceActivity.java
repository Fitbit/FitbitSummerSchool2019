package com.fitbit.blesession;

import androidx.appcompat.app.AppCompatActivity;
import no.nordicsemi.android.thingylib.ThingyListener;
import no.nordicsemi.android.thingylib.ThingySdkManager;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;

public class ThingyDeviceActivity extends AppCompatActivity implements  ThingySdkManager.ServiceConnectionListener {
    private static final String TAG = ThingyDeviceActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thingy_device);
        // TODO 2: Get the BluetoothDevice from the intent
        // TODO 3: Get an instance of ThingySdkManager using ThingySdkManager.getInstance()
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        // TODO 4a: Bind the MyThingyService to the ThingySdkManager
        // TODO 6a: Register your custom ThingyListener
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        // TODO 4b: Unbind the MyThingyService
        // TODO 6a: Unregister your custom ThingyListener
    }

    @Override
    public void onServiceConnected() {
        Log.d(TAG, "Thingy Service Connected");
        // TODO 7: Connect to the ThingyBoard using the ThingySdkManager object
    }
}
