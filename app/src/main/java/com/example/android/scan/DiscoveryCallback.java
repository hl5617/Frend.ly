package com.example.android.scan;
import android.bluetooth.BluetoothDevice;

public interface DiscoveryCallback {
        void onDiscoveryStarted();
        void onDiscoveryFinished();
        void onDeviceFound(BluetoothDevice device);
        void onDevicePaired(BluetoothDevice device);
        void onDeviceUnpaired(BluetoothDevice device);
        void onError(String message);
}
