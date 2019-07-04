package com.fitbit.blesession;

import com.fitbit.blesession.bluetooth.BluetoothController;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_PERMISSION_BLUETOOTH_LE_SCAN = 11;

    public static final String BLUETOOTH_DEVICE_KEY = "com.fitbit.summerschool.BLUETOOTH_DEVICE_KEY";

    private Button bleScanButton;

    private List<BluetoothDevice> devicesList = new ArrayList<>();
    private DevicesListAdapter devicesListAdapter;

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
        bluetoothController.cleanUp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void initViews() {
        // Scanning button
        bleScanButton = findViewById(R.id.scan_ble_button);
        bleScanButton.setOnClickListener(new BleScanButtonClickListener());

        // Devices List
        RecyclerView devicesRecyclerView = findViewById(R.id.devices_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        devicesRecyclerView.setLayoutManager(layoutManager);
        devicesListAdapter = new DevicesListAdapter(this, devicesList);
        devicesRecyclerView.setAdapter(devicesListAdapter);

        // Set Adapter click listener
        devicesListAdapter.setClickListener(
                new DevicesListAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO 1a: Create an Intent object in order to navigate to ThingyDeviceActivity
                        // TODO 1b: Get the bluetooth device from the user selection and attach it to the intent (hint: use .putExtra() method)
                        // TODO 1c: Navigate to ThingyDeviceActivity using startActivity(intent: ) method.
                    }
                }
        );
    }

    private class BleScanButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // If a scan is started the button shows the text Stop scan, so we stop the scan
            if (bluetoothController.isScanInProgress()) {
                bluetoothController.cleanUp(MainActivity.this);
                bleScanButton.setText(R.string.scan_ble_devices);
                return;
            }

            // Start a scan
            if (checkAndRequestPermissionForScanning()) {
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
    }

    /**
     * Since Android 6 we need the location permission to be granted at runtime in order to receive
     * results from scanning. If we don't do this check, we are allowed to call the API methods for
     * starting a scan but we won't receive any results.
     */
    private boolean checkAndRequestPermissionForScanning() {
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
                        MainActivity.REQUEST_PERMISSION_BLUETOOTH_LE_SCAN);
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
            if (device.getName() != null) {
                devicesList.add(device);
                devicesListAdapter.notifyItemInserted(devicesList.size() - 1);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.w(TAG, "BLE SCAN FAILED");
        }
    }
}
