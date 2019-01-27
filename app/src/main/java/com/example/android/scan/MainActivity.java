package com.example.android.scan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context = getApplicationContext();

    ConnectionsClient connectionsClient = Nearby.getConnectionsClient(context);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    gotoProfile();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        Nearby.getConnectionsClient(context).stopAllEndpoints();

        super.onStop();
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, 0, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }

    public void gotoProfile() {
        Context context = MainActivity.this;
        Class destinationActivity = Profile.class;
        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    public void disconnect(View view) {
        connectionsClient.disconnectFromEndpoint("");
    }



//
//    public List<String> checkHosts(String subnet) throws IOException {
//
//        InetAddress localHost = Inet4Address.getLocalHost();
//        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
//        short temp = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
//        System.out.println(temp);
//
//
//
//        List<String> results = new ArrayList<>();
//
//        int timeout = 1000;
//
//        for (int i = 1; i < 255; i++) {
//            String host = subnet + "." + i;
//            if (InetAddress.getByName(host).isReachable(timeout)) {
//                results.add(host);
//            }
//
//        }
//        return results;
//
//    }

}
