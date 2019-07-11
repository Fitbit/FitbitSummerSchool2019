package com.fitbit.blesession.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import androidx.annotation.Nullable;
import no.nordicsemi.android.thingylib.BaseThingyService;
import no.nordicsemi.android.thingylib.ThingyConnection;

public class MyThingyService extends BaseThingyService {
    private ThingyBinder thingyBinder = new ThingyBinder();

    @Nullable
    @Override
    public BaseThingyBinder onBind(Intent intent) {
        return thingyBinder;
    }

    public class ThingyBinder extends BaseThingyBinder {
        @Override
        public ThingyConnection getThingyConnection(BluetoothDevice device) {
            return mThingyConnections.get(device);
        }
    }
}
