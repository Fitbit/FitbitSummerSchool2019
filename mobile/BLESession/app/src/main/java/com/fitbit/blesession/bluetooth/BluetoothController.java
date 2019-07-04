package com.fitbit.blesession.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.fitbit.blesession.R;

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

    public static final int BT_REQUEST_CODE_FOR_BLE = 43;

    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback bleScanCallback;

    private boolean ongoingBleScan = false;

    public BluetoothController() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

    /**
     * MUST call this when stopping or destroying the activity.
     * <p>
     * Stops any ongoing scan, unregisters the broadcast receiver.
     *
     * @param activity we need the context for unregistering the receiver
     */
    public void cleanUp(Activity activity) {
        stopBleScan();
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
    private void stopBleScan() {
        if (bleScanCallback != null) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(bleScanCallback);
            bleScanCallback = null;
            ongoingBleScan = false;
        }
    }
}
