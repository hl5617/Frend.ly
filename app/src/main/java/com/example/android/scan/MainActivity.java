package com.example.android.scan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.android.scan.Profile.PROFILE_FILENAME;

public class MainActivity extends AppCompatActivity {

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

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private List<String> hobbies;
    private String name;
    private String otherName;
    private File profile;
    private File otherProfile;
    private List<String> otherHobbies;
    private String[] possibleHobbies;

    private String opponentName;

    Payload payload_g;

    private static final String TAG = "FREND.LY";

    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;



    //Our Strategy
    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;

    //Our handle to Nearby Clients
    private ConnectionsClient connectionsClient;

    private String otherID;

    private Button disconnectButton;
    private Button connectButton;
    private Button loadButton;

    private TextView statusText;
    private TextView otherText;

    // Callbacks for receiving payloads
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    Log.d("RE", "RECEIVING THE PAYLOAD\n");
                    connectionsClient.stopDiscovery();
                    payload_g = payload;
                    otherProfile = payload_g.asFile().asJavaFile();
                    try {
                        readOtherProfile();
                    } catch (IOException e) {
                        Log.d("ERR", "DLKJFLKDSJF");
                    }
                    prepareListData();
                    /*try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter("otherProfile.txt"));
                        writer.write(otherProfile.toString());
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("File is empty\n");
                    }*/
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                }
            };

    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    connectionsClient.requestConnection(name, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {}
            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    opponentName = connectionInfo.getEndpointName();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");

                        try {
                            Log.d("PROGILE", "WE ARE SENDING THEPROFILE\n");
                            sendFile(profile);
                        } catch (FileNotFoundException e) {}

//                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();

                        otherID = endpointId;
                        setOpponentName(opponentName);
                        //setStatusText(getString(R.string.status_connected));
//                        setButtonState(true);
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                }
            };

    private void readMyProfile() throws IOException {
        //mTestView.setText("readMyProfile");
        hobbies = new ArrayList<>();
        FileInputStream is;
        BufferedReader reader;
        if (profile.exists()) {
            is = new FileInputStream(profile);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            if (line != null) {
                name = line;
            }
            while(line != null){
                line = reader.readLine();
                if (line != null) {
                    hobbies.add(possibleHobbies[Integer.parseInt(line)]);
                }
            }
            reader.close();
            is.close();
        }
    }

    private void readOtherProfile() throws IOException {
        //mTestView.setText("readMyProfile");
        Log.d("YESSS", "READDAD\n");
        otherHobbies = new ArrayList<>();
        FileInputStream is;
        BufferedReader reader;
        if (otherProfile.exists()) {
            is = new FileInputStream(otherProfile);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            if (line != null) {
                otherName = line;
            }
            Log.d("YESSS", "READDAD345\n");
            while(line != null){
                line = reader.readLine();
                if (line != null) {
                    otherHobbies.add(possibleHobbies[Integer.parseInt(line)]);
                }
            }
            reader.close();
            is.close();
        }
    }

    private void giveNoName() throws IOException {
        name = "name not set";
        final PrintWriter writer = new PrintWriter(profile);

        writer.println(name);
        writer.close();
    }

    public void prepareFakeListData() {
        Log.d("YESSS", "FAKe PREPARE\n");
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        addDude("Konstantinos",
                Arrays.asList("Tea bag collecting", "Cross-stitch", "Dance", "Metalworking", "Lapidary", "BMX"));

    }

    private void prepareListData() {
        Log.d("YESSS", "PREPARE\n");
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        addDude(otherName, otherHobbies);

        /*List<String> steveHobbies = new ArrayList<String>();
        steveHobbies.add("Tea bag collecting");
        steveHobbies.add("Bird watching");
        steveHobbies.add("Amateur yodelling");

        List<String> waldoHobbies = new ArrayList<String>();
        waldoHobbies.add("Gunsmithing");
        waldoHobbies.add("Swimming");
        waldoHobbies.add("Hunting");*/

        //addDude("waldo", waldoHobbies);
        //addDude("steve", steveHobbies);
    }


    private void prepareJunkListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataChild.put("THIS SHOULD NOT SHOWWW", new ArrayList<String>());

        /*List<String> steveHobbies = new ArrayList<String>();
        steveHobbies.add("Tea bag collecting");
        steveHobbies.add("Bird watching");
        steveHobbies.add("Amateur yodelling");

        List<String> waldoHobbies = new ArrayList<String>();
        waldoHobbies.add("Gunsmithing");
        waldoHobbies.add("Swimming");
        waldoHobbies.add("Hunting");*/

        //addDude("waldo", waldoHobbies);
        //addDude("steve", steveHobbies);
    }

    private void addDude(String name, List<String> theirHobbies) {
        listDataHeader.add(name);
        List<String> theirHobbiesFiltered = new ArrayList<>();
        for (String h : theirHobbies) {
            if (hobbies.contains(h)) {
                theirHobbiesFiltered.add(h);
            }
        }
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), theirHobbiesFiltered);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        possibleHobbies = (String[]) getResources().getStringArray(R.array.hobby_array);

        profile = new File(this.getApplicationContext().getFilesDir(), PROFILE_FILENAME);

        if (profile.exists()) {
            try {
                readMyProfile();
            } catch (IOException e){
                Log.d("ERR", "IOEXc");
            }
        } else {
            try {
                profile.createNewFile();
                hobbies = new ArrayList<>();
                giveNoName();
            } catch (IOException e) {
                Log.d("ERR", "IOEXc");
            }
        }


        disconnectButton = findViewById(R.id.disconnect);
        connectButton = findViewById(R.id.connect);
        loadButton = findViewById(R.id.button_load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareFakeListData();
            }
        });

        /*connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prepareExampleListData();
            }
        });*/

        //statusText = findViewById(R.id.status);

        //TextView nameView = findViewById(R.id.name);
        //nameView.setText(getString(R.string.codename, name));

        connectionsClient = Nearby.getConnectionsClient(this);


        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        prepareJunkListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    @Override
    protected void onStop() {
        connectionsClient.stopAllEndpoints();
        super.onStop();
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Handles user acceptance (or denial) of our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                //Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }


    /** Finds an Frend using Nearby Connections. */
    public void findFrend(View view) {
        Log.d("IHIH", "sflngkfgnldfgndflkgndlfkgndflkgndlfkgndl\n");
        startAdvertising();
        startDiscovery();
        Log.d("IHIH", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH\n");
        //setStatusText(getString(R.string.status_searching));
    }

    public void disconnect(View view) {
        connectionsClient.disconnectFromEndpoint(otherID);
    }


    /** Starts looking for Frends using Nearby Connections. */
    private void startDiscovery() {
        // Note: Discovery may fail.
        connectionsClient.startDiscovery(
                getPackageName(), endpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(STRATEGY).build());
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        // Note: Advertising may fail.
        connectionsClient.startAdvertising(
                name, getPackageName(), connectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build());
    }


    /** Sends the user's file to the Frend. */
    private void sendFile(File file) throws FileNotFoundException {

        connectionsClient.sendPayload(
                otherID, Payload.fromFile(file));

        Log.d("SEND", "LITERALY SENDING FILE\n");
        //setStatusText(getString(R.string.game_choice, choice.name()));
//        connectButton.setEnabled(false);
    }

    /** Shows a status message to the user. */
    private void setStatusText(String text) {
        statusText.setText(text);
    }

    /** Updates the opponent name on the UI. */
    private void setOpponentName(String opponentName) {
        //otherText.setText(getString(R.string.opponent_name, opponentName));
    }

    /** Enables/disables buttons depending on the connection status. */
    private void setButtonState(boolean connected) {
        connectButton.setEnabled(true);
        connectButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        disconnectButton.setVisibility(connected ? View.VISIBLE : View.GONE);
    }

    public void gotoProfile() {
        Context context = MainActivity.this;
        Class destinationActivity = Profile.class;
        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

}
