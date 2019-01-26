package com.example.android.scan;

import android.app.AppComponentFactory;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Looper.getMainLooper;

public class WiFi extends AppCompatActivity {

    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;

    private final IntentFilter intentFilter = new IntentFilter();

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Indicates a change in the Wi-Fi P2P status.
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//
//        // Indicates a change in the list of available peers.
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//
//        // Indicates the state of Wi-Fi P2P connectivity has changed.
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//
//        // Indicates this device's details have changed.
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//
//        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//        mChannel = mManager.initialize(, getMainLooper(), null);
//
//    }

    public void test() throws IOException {
        Context context = this.getApplicationContext();
        File file = new File(context.getFilesDir(), "test.txt");
        try {
            file.createNewFile();
        } catch(IOException e) {
        }

        FileOutputStream fo = new FileOutputStream(file);


//        ByteArrayOutputStream byteArrayOutputStream =


    }

}
