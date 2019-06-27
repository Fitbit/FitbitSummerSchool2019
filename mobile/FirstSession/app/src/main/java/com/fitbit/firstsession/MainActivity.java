package com.fitbit.firstsession;

import com.fitbit.firstsession.bluetooth.BluetoothController;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_PERMISSION_BLUETOOTH_SCAN = 10;
    private static final int REQUEST_PERMISSION_BLUETOOTH_LE_SCAN = 11;

    public static final String FOUND_NEW_DEVICE_ACTION = "com.fitbit.summerschool.FOUND_NEW_DEVICE_ACTION";
    public static final String FOUND_NEW_DEVICE_EXTRA = "com.fitbit.summerschool.FOUND_NEW_DEVICE_EXTRA";


    private Button scanButton;
    private Button bleScanButton;

    private ArrayList<String> devicesList = new ArrayList<>();
    private RecyclerView.Adapter devicesListAdapter;

    private BroadcastReceiver resultsBroadcastReceiver;
    private BluetoothController bluetoothController = new BluetoothController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(resultsBroadcastReceiver);
        bluetoothController.cleanUp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void initViews() {
        // Scanning buttons
        scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new ScanButtonClickListener());

        bleScanButton = findViewById(R.id.scan_ble_button);
        bleScanButton.setOnClickListener(new BleScanButtonClickListener());

        // Devices List
        RecyclerView devicesRecyclerView = findViewById(R.id.devices_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        devicesRecyclerView.setLayoutManager(layoutManager);
        devicesListAdapter = new DevicesListAdapter(this, devicesList);
        devicesRecyclerView.setAdapter(devicesListAdapter);

        // Receive the devices' info as intents
        registerResultsReceiver();
    }

    private class ScanButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // If a scan was already triggered then the button shows the text Stop scan, so we stop the scan
            if (bluetoothController.isScanInProgress()) {
                bluetoothController.cleanUp(MainActivity.this);
                scanButton.setText(R.string.scan_devices);
                bleScanButton.setClickable(true);
                return;
            }

            if (checkAndRequestPermissionForScanning(REQUEST_PERMISSION_BLUETOOTH_SCAN)) {
                startClassicBluetoothScan();
            }
        }
    }

    private class BleScanButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            // If a scan is started the button shows the text Stop scan, so we stop the scan
            if (bluetoothController.isScanInProgress()) {
                bluetoothController.cleanUp(MainActivity.this);
                bleScanButton.setText(R.string.scan_ble_devices);
                scanButton.setClickable(true);
                return;
            }

            // Start a scan
            if (checkAndRequestPermissionForScanning(REQUEST_PERMISSION_BLUETOOTH_LE_SCAN)) {
                startBluetoothLEScan();
            }
        }
    }

    private void startBluetoothLEScan() {
        if (!bluetoothController.checkBLE(this)) {
            return;
        }

        bluetoothController.startBleScan(MainActivity.this, new LeScanCallback());

        // clear UI
        devicesList.clear();
        devicesListAdapter.notifyDataSetChanged();

        bleScanButton.setText(R.string.stop_scan);
        scanButton.setClickable(false);
    }

    private void startClassicBluetoothScan() {
        if (!bluetoothController.checkAndEnableBluetooth(this, BluetoothController.BT_REQUEST_CODE)) {
            return;
        }
        bluetoothController.startScan(MainActivity.this);

        // clear UI
        devicesList.clear();
        devicesListAdapter.notifyDataSetChanged();
        scanButton.setText(R.string.stop_scan);
        bleScanButton.setClickable(false);
    }

    /**
     * Used for receiving the results of a classic bluetooth scan from the BluetoothReceiver.
     * <br>
     * This flow is just for exercising the local Intents-BroadcastReceiver concepts.
     * It would have sufficed to have the BluetoothReceiver directly as an internal class here,
     * with access to the devicesList.
     */
    private void registerResultsReceiver() {
        resultsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String deviceInfo = intent.getStringExtra(FOUND_NEW_DEVICE_EXTRA);

                if (deviceInfo == null) {//scan has finished
                    scanButton.setText(R.string.scan_devices);
                    bleScanButton.setClickable(true);
                }

                devicesList.add(deviceInfo);
                devicesListAdapter.notifyItemInserted(devicesList.size() - 1);
            }
        };

        // Using LocalBroadcastManager because these intents are sent and received only inside our app
        LocalBroadcastManager.getInstance(this).registerReceiver(resultsBroadcastReceiver,
                new IntentFilter(FOUND_NEW_DEVICE_ACTION));
    }

    /**
     * Since Android 6 we need the location permission to be granted at runtime in order to receive
     * results from scanning. If we don't do this check, we are allowed to call the API methods for
     * starting a scan but we won't receive any results.
     *
     * @param request_code a constant defined by us used for identifying the request in the result callback.
     *                     In this case, we differentiate between the types of scan based on this code.
     */
    private boolean checkAndRequestPermissionForScanning(int request_code) {
        // Request permission, the scan will be started when it is granted
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Toast.makeText(MainActivity.this,
                        "Cannot scan without granting location permission.",
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        request_code);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSION_BLUETOOTH_SCAN:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Permission granted, starting BT scan",
                            Toast.LENGTH_LONG).show();
                    startClassicBluetoothScan();
                } else {
                    Toast.makeText(this,
                            "Permission not granted, cannot scan for BT devices",
                            Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_PERMISSION_BLUETOOTH_LE_SCAN:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Permission granted, starting BLE scan",
                            Toast.LENGTH_LONG).show();
                    startBluetoothLEScan();
                } else {
                    Toast.makeText(this,
                            "Permission not granted, cannot scan for BT devices",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case BluetoothController.BT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    startClassicBluetoothScan();
                }
                break;
            case BluetoothController.BT_REQUEST_CODE_FOR_BLE:
                if (resultCode == RESULT_OK) {
                    startBluetoothLEScan();
                }
                break;
        }
    }

    /**
     * Callback for receiving Bluetooth Low Energy scan results. We provide an instance
     * of this class when starting a scan, and the system will call its method to send the
     * results back to us.
     */
    private class LeScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            devicesList.add(String.format("%s %s", device.getName(), device.getAddress()));
            devicesListAdapter.notifyItemInserted(devicesList.size() - 1);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.w(TAG, "BLE SCAN FAILED");
        }
    }
}
