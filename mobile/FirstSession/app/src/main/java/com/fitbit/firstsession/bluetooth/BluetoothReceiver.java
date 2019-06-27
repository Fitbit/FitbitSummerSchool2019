package com.fitbit.firstsession.bluetooth;

import com.fitbit.firstsession.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Receiver for Bluetooth events. The types of events are provided as an IntentFilter when configuring
 * and registering this receiver.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothController.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast");

        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceMAC = device.getAddress();

            Log.i(TAG, String.format("Discovered device: %sMAC = %s", deviceName, deviceMAC));

            informActivity(context, deviceName + " " + deviceMAC);
        }

        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.i(TAG, "Finished scanning!");
            informActivity(context, null);
        }
    }

    private void informActivity(Context context, String deviceInfo) {
        Intent intent = new Intent(MainActivity.FOUND_NEW_DEVICE_ACTION);
        Bundle extras = new Bundle();
        extras.putString(MainActivity.FOUND_NEW_DEVICE_EXTRA, deviceInfo);
        intent.putExtras(extras);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
