package com.example.android.scan;

public interface BluetoothCallback {
        void onBluetoothTurningOn();
        void onBluetoothOn();
        void onBluetoothTurningOff();
        void onBluetoothOff();
        void onUserDeniedActivation();
}
