/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jobesk.nourv.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.vision.barcode.Barcode;
import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.SessionManager;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import static android.app.Notification.PRIORITY_MIN;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service implements Serializable {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final UUID ALERT_LEVEL_UUID = UUID.fromString("00002A06-0000-1000-8000-00805f9b34fb");

    //public static final String alertLevel="";

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_ACL_CONNECTED =
            "com.example.bluetooth.le.ACTION_ACL_CONNECTED";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("LongLogTag")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("STATE CONNECTED", "OK");
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i("STATE CONNECTED BROADCAST SENT - DISCOVERING SERVICES", "OK");
                // Attempts to discover services after successful connection.
                mBluetoothGatt.discoverServices();
                new DownloadLink().execute();
                Log.i("AFTER DISCOVER SERVICES", "OK");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("STATE DISCONNECTED", "OK");
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
                Log.i("STATE DISCONNECTED BROADCAST SENT", "OK");
            }

            if (mConnectionState == 123334) {

            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.i("GATT SUCCESS - SERVICES DISCOVERED", "OK");
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
               /* int battery = 0;
                try {
                    String hex = new String(characteristic.getValue(), "UTF-8");
                    battery = Integer.parseInt(hex, 16);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("battery", "onCharacteristicRead: " + battery);*/
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            broadcastUpdate(ACTION_ACL_CONNECTED, rssi);
            Log.d("rsssi", "onReadRemoteRssi: "+rssi);
            Log.d("rsssi", "onReadRemoteRssi: "+calculateDistance(rssi));
        }

    };

    class DownloadLink extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
//                try {
//                    Thread.sleep(30000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                readRssi();
                return null;
            }
        }
    }

    /*@Override
    public void onCreate() {
        super.onCreate();
        //sessionManager = new SessionManager(this);
        Intent intent = new Intent(this, BluetoothLeService.class);
        startService(intent);
        String channelId = "myyyyyy";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service");
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.app_icon)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .setContentText("Bluetooth Service is running")
                .setAutoCancel(true)
                .build();
        startForeground(102, notification);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_MIN);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);
        manager.cancelAll();
        return channelId;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }*/
    public static byte[] parseHexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len - 1; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
        Log.i("INTENT SENT", "OK");
    }
    private void broadcastUpdate(final String action,int rssi) {
        final Intent intent = new Intent(action);
        intent.putExtra(action, rssi);
        sendBroadcast(intent);
        Log.i("INTENT SENT", "OK");
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            /*final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());*/
            intent.putExtra(EXTRA_DATA,characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
            Log.d(TAG, "broadcastUpdate: "+characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        setCharacteristicNotification(characteristic,true);
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void readRssi() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readRemoteRssi();
    }

    public void writeCharacteristic(String type) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w("nnnnn", "BluetoothAdapter not initialized");
            return;
        }
        if (type.equalsIgnoreCase("buzzer")) {
            BluetoothGattService Service = mBluetoothGatt.getService(UUID.fromString(getResources().getString(R.string.BUZZER_SERVICE)));
            if (Service == null) {
                Log.e(TAG, "service not found!");
                return;
            }
            BluetoothGattCharacteristic characteristic = Service
                    .getCharacteristic(UUID.fromString(getResources().getString(R.string.ALERT_LEVEL)));
            if (characteristic == null) {
                Log.e(TAG, "char not found!");
                return;
            }

            /****Working Code Starts*/
            byte[] value = new byte[]{0x01};
            characteristic.setValue(value);
            Log.w("nnnnn", "BluetoothAdapter not initialized" + value);
            mBluetoothGatt.writeCharacteristic(characteristic);
            /****Working Code Ends*/
        }else if (type.equalsIgnoreCase("battery")) {
            BluetoothGattService Service = mBluetoothGatt.getService(UUID.fromString(getResources().getString(R.string.BATTERY_SERVICE)));
            if (Service == null) {
                Log.e(TAG, "service not found!");
                return;
            }
            BluetoothGattCharacteristic characteristic = Service
                    .getCharacteristic(UUID.fromString(getResources().getString(R.string.CHAR_BATTERY_LEVEL)));
            if (characteristic == null) {
                Log.e(TAG, "char not found!");
                return;
            }
             /*int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }*/
            readCharacteristic(characteristic);
            /****Working Code Starts*/
            //mBluetoothGatt.writeCharacteristic(characteristic);
            /****Working Code Ends*/
        }
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    public Barcode.GeoPoint getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Barcode.GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new Barcode.GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;

    }
    public double calculateDistance(double rssi) {
        if (rssi == 0) {
            return -1.0;
         }
            double ratio = rssi * 1.0 / 4;
            if (ratio < 1.0) {
                Log.d("rsssi", "onReadRemoteRssi: if: "+Math.pow(ratio, 10));
                return Math.pow(ratio, 10);
            } else {
                double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
                Log.d("rsssi", "onReadRemoteRssi: else: "+accuracy);
                return accuracy;
            }
        }
}