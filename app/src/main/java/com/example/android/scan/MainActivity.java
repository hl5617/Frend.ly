package com.example.android.scan;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    public void sendString(View view) {
        Bluetooth bluetooth = new Bluetooth(view.getContext());

        EditText edit = findViewById(R.id.editText);

        String string = edit.getText().toString();


        bluetooth.onStart();
        bluetooth.enable();
        bluetooth.startScanning();

        List<BluetoothDevice> deviceList = bluetooth.getPairedDevices();

        for (BluetoothDevice dev : deviceList) {
            bluetooth.pair(dev);
            bluetooth.connectToDevice(dev);
            bluetooth.send(string);
            bluetooth.disconnect();
            bluetooth.unpair(dev);
        }

        bluetooth.stopScanning();
        bluetooth.onStop();

//        TextView textView = findViewById(R.id.textView);
//
//        textView.setText(string);
    }

    public void receiveString(View view) {
        Bluetooth bluetooth = new Bluetooth(view.getContext());

        bluetooth.onStart();
        bluetooth.enable();
        bluetooth.startScanning();



    }


}
