package com.example.android.scan;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public abstract class GAPI extends AppCompatActivity {

    private static final String TAG = "FRENDS";

    String endpointId;

    private TextView statusText;

    Payload payload;

    Context context = getApplicationContext();

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    /** Our handler to Nearby Connections. */
    private ConnectionsClient mConnectionsClient;

    /** The devices we've discovered near us. */
    private final Map<String, ConnectionsActivity.Endpoint> mDiscoveredEndpoints = new HashMap<>();


    private final Map<String, ConnectionsActivity.Endpoint> mPendingConnections = new HashMap<>();

    /**
     * The devices we are currently connected to. For advertisers, this may be large. For discoverers,
     * there will only be one entry in this map.
     */
    private final Map<String, ConnectionsActivity.Endpoint> mEstablishedConnections = new HashMap<>();

    /**
     * True if we are asking a discovered device to connect to us. While we ask, we cannot ask another
     * device.
     */
    private boolean mIsConnecting = false;

    /** True if we are discovering. */
    private boolean mIsDiscovering = false;

    /** True if we are advertising. */
    private boolean mIsAdvertising = false;


    private String getUserNickname() {
        return "same";
    }


    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload load) {
        payload = load;
    }



    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    // A new payload is being sent over.
                    setPayload(payload);
                    Toast.makeText(context, 0, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    // Payload progress has updated.

                }
            };

    public void setEndpointId(String id) {
        endpointId = id;
    }

    private void setStatusText(String text) {
        statusText.setText(text);
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection on both sides.
                    Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            Log.i(TAG, "onConnectionResult: connection successful");

                            Nearby.getConnectionsClient(context).stopDiscovery();
                            Nearby.getConnectionsClient(context).stopAdvertising();

                            setEndpointId(endpointId);
                            setStatusText(getString(0));
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            break;
                        default:
                            // The connection was broken before it was accepted.
                            break;
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                }
            };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // An endpoint was found. We request a connection to it.
                    Nearby.getConnectionsClient(context)
                            .requestConnection(getUserNickname(), endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(
                                    (Void unused) -> {
                                        // We successfully requested a connection. Now both sides
                                        // must accept before the connection is established.
                                    })
                            .addOnFailureListener(
                                    (Exception e) -> {
                                        // Nearby Connections failed to request the connection.
                                    });
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
            };


    private void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery("com.example.android.scan", endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're discovering!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                        });
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        getUserNickname(), "com.example.android.scan", connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're advertising!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We were unable to start advertising.
                        });
    }

    /** Stops advertising. */
    protected void stopAdvertising() {
        mIsAdvertising = false;
        mConnectionsClient.stopAdvertising();
    }

    /** Returns {@code true} if currently advertising. */
    protected boolean isAdvertising() {
        return mIsAdvertising;
    }

    @Override
    public void onConnectionInitiated(String endpointId, ConnectionInfo info) {
        new AlertDialog.Builder(context)
                .setTitle("Accept connection to " + info.getEndpointName())
                .setMessage("Confirm the code matches on both devices: " + info.getAuthenticationToken())
                .setPositiveButton(
                        "Accept",
                        (DialogInterface dialog, int which) ->
                                // The user confirmed, so we can accept the connection.
                                Nearby.getConnectionsClient(context)
                                        .acceptConnection(endpointId, payloadCallback))
                .setNegativeButton(
                        android.R.string.cancel,
                        (DialogInterface dialog, int which) ->
                                // The user canceled, so we should reject the connection.
                                Nearby.getConnectionsClient(context).rejectConnection(endpointId))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void getConnection(String endpointId) {
        Nearby.getConnectionsClient(context)
                .requestConnection(endpointId, endpointId,connectionLifecycleCallback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    public void onSuccess(Void aVoid) {
                        Log.d("ERR", "Requesting connection..");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERR", "Error requesting connection", e);
                    }
                });
    }

    public void transfer() {
        Nearby.getConnectionsClient(this).sendPayload(endpointId, payload);
    }



















}
