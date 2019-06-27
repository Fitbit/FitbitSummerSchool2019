package com.fitbit.firstsession.bluetooth;

import com.fitbit.firstsession.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Utility class for interacting with the Bluetooth API.
 * <br>
 * It starts and stops scans.
 * <br>
 * As state, it keeps the registered broadcast receiver, some BLE related info.
 * <br>
 * Can be transformed in an Android Service (this concept is not addressed in the first session).
 */
public class BluetoothController {

    private static final String TAG = BluetoothController.class.getName();

    public static final int BT_REQUEST_CODE = 42;
    public static final int BT_REQUEST_CODE_FOR_BLE = 43;


    private BroadcastReceiver bluetoothReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback bleScanCallback;

    private boolean ongoingBleScan = false;

    public BluetoothController() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startScan(Activity activity) {
        if (!checkAndEnableBluetooth(activity, BT_REQUEST_CODE)) {
            return;
        }

        Log.i(TAG, "Starting scan");

        // Register the broadcast receiver that will receive the scan's results
        registerReceiver(activity);

        // Triggers a scan. This method returns immediately, the results will be provided by
        // the system as Intents in the following tens of seconds (around a minute).
        bluetoothAdapter.startDiscovery();
    }

    public boolean isScanInProgress() {
        return bluetoothAdapter.isDiscovering() || ongoingBleScan;
    }

    /**
     * Checks if bluetooth is supported and enabled.
     * If it is not enabled we prompt the user to enable it. The result of the user's action is
     * received in the activity. The activity will call again the startScan method of this class.
     *
     * @param activity used for showing a dialog to the user to turn on Bluetooth
     * @return false if it is not supported or not enabled yet.
     */
    public boolean checkAndEnableBluetooth(Activity activity, int requestCode) {
        if (bluetoothAdapter == null) { //e.g. if you are in an emulator
            Log.i(TAG, "Device doesn't support Bluetooth");
            Toast.makeText(activity, R.string.bt_not_supported, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, requestCode);
            return false;
        }
        return true;
    }

    public boolean checkBLE(Activity activity) {

        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            return false;
        }

        return checkAndEnableBluetooth(activity, BT_REQUEST_CODE_FOR_BLE);
    }

    private void registerReceiver(Context context) {
        if (bluetoothReceiver == null) {
            bluetoothReceiver = new BluetoothReceiver();
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(bluetoothReceiver, intentFilter);
    }

    private void unregisterReceiver(Context context) {
        if (bluetoothReceiver != null) {
            context.unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
    }

    /**
     * MUST call this when stopping or destroying the activity.
     * <p>
     * Stops any ongoing scan, unregisters the broadcast receiver.
     *
     * @param activity we need the context for unregistering the receiver
     */
    public void cleanUp(Activity activity) {
        // stop any ongoing scan
        if (bluetoothReceiver != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        stopBleScan();
        // unregister any receiver
        unregisterReceiver(activity);
    }

    /**
     * Starts a Bluetooth Low Energy scan if Bluetooth is enabled.
     */
    public void startBleScan(Activity activity, ScanCallback bleScanCallback) {
        if (!checkBLE(activity)) {
            return;
        }

        this.bleScanCallback = bleScanCallback;
        bluetoothAdapter.getBluetoothLeScanner().startScan(bleScanCallback);
        ongoingBleScan = true;
    }

    /**
     * Stops an ongoing Bluetooth Low Energy scan
     */
    public void stopBleScan() {
        if (bleScanCallback != null) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(bleScanCallback);
            bleScanCallback = null;
            ongoingBleScan = false;
        }
    }
}
