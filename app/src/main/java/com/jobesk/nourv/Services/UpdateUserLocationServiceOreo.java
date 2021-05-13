package com.jobesk.nourv.Services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.NestedScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.jobesk.nourv.Activities.MainActivity;
import com.jobesk.nourv.FirebaseServices.BluetoothDeviceService;
import com.jobesk.nourv.FirebaseServices.ChangeEventListener;
import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.Constants;
import com.jobesk.nourv.Utils.MyLocation;
import com.jobesk.nourv.Utils.MySingleton;
import com.jobesk.nourv.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Notification.PRIORITY_MIN;


public class UpdateUserLocationServiceOreo extends Service {

    private static final String TAG = "UpdateUserLocationServi";
    SessionManager sessionManager;

    /******/
    private BluetoothLeService mBluetoothLeService;
    private BluetoothLeService tempInstance;
    private String mDeviceAddress, deviceId, deviceTitle;
    private String battery = "";
    private Intent gattServiceIntent;
    private boolean isServiceConnected = false;
    private NestedScrollView device_detail;
    private int rssi = -1, count = 0;
    private MainActivity mainActivity;
    GPSTracker gpsTracker;
    String latitude = "0.0", longitude = "0.0";
    int increment = 4;
    MyLocation myLocation = new MyLocation();
    boolean isDisconnected = false;
    public static boolean isRunning = false;
    public static UpdateUserLocationServiceOreo instance = null;
    /******/

    //FOR NOTIFICATIONS
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAANPVIjcs:APA91bF7vG7d0Duois7UJ8I8ojoGZbGwgA01TBUT1H6CV5vDorAUpgV4Ht2xz4NBvUeeIGbR6gFHPH9CV6UfnKwCGwaoCalXsKiBISZL80TZJGMTQnX-Qpayc4ovM_E6o4FYaTy6rYYj";
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String USER_ID;
    String TOPIC = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        isRunning = true;
        sessionManager = new SessionManager(this);
        registerReceiver();
        String channelId = "myyyyyy";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service");
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L});
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.app_icon)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .setVibrate(new long[]{0L})
                .setContentText("Nour is running in background")
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(101, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        } else {
            startForeground(101, notification);
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        chan.setVibrationPattern(new long[]{0L});
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);
        manager.cancelAll();
        return channelId;
    }

    //private final IBinder mBinder = new UpdateUserLocationServiceOreo.LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*public class LocalBinder extends Binder {
        public UpdateUserLocationServiceOreo getService() {
            return UpdateUserLocationServiceOreo.this;
        }
    }
*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("uuuuuuuuuuuuuu", "onStartCommand: oreo service is started");
        Intent myIntent = intent; // this getter is just for example purpose, can differ
        if (myIntent != null && myIntent.getExtras() != null) {
            mDeviceAddress = myIntent.getExtras().getString("mDeviceAddress");
            deviceId = myIntent.getExtras().getString("deviceId");
            deviceTitle = myIntent.getExtras().getString("deviceTitle");
        }
        Log.d("uuuuuuuuuuuuuu", "onStartCommand: mDeviceAddress: " + mDeviceAddress);

        /* *//**********//*
        Intent mIntent = new Intent(UpdateUserLocationServiceOreo.this, BluetoothLeService.class);
        if (!isMyServiceRunning(BluetoothLeService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mIntent);
            } else {
                startService(mIntent);
            }
        } else {
            UpdateUserLocationServiceOreo.this.stopService(mIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mIntent);
            } else {
                startService(mIntent);
            }
        }
        *//**********/
        gattServiceIntent = new Intent(getBaseContext(), BluetoothLeService.class);
        if (isServiceConnected) unbindService(mServiceConnection);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        instance = null;
        unbindService(mServiceConnection);
        unRegisterReceiver();
        count = 0;
    }

    public void bleBuzzer() {
        Log.d(TAG, "bleBuzzer: " + mBluetoothLeService);
        mBluetoothLeService.writeCharacteristic("buzzer");
    }

    /***************/
    // Code to manage Service lifecycle.
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            UpdateUserLocationServiceOreo.this.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.d(TAG, "onServiceConnected: " + mBluetoothLeService);
            Log.d(TAG, "componentName: " + componentName);
            mBluetoothLeService.initialize();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                //context.finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.d(TAG, "onServiceConnected: " + mDeviceAddress);
            mBluetoothLeService.connect(mDeviceAddress);
            count = 0;
            isServiceConnected = true;
            isDisconnected = false;

            /********/
            Handler handler = new Handler();
            int delay = 10000; //milliseconds

            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    mBluetoothLeService.writeCharacteristic("battery");
                    mBluetoothLeService.readRssi();
                    Log.d(TAG, "run: connected");
                    handler.postDelayed(this, delay);
                }
            }, delay);
            /********/
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: " + componentName);
            mBluetoothLeService = null;
            isServiceConnected = false;
            isDisconnected = true;

        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateLocation() {
        myLocation.getLocation(getApplicationContext(), locationResult);
        BluetoothDeviceService service = new BluetoothDeviceService();
        service.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        Log.d(TAG, "updateLocation: going to update location");
        Log.d(TAG, "sessionManager.getLat()" + sessionManager.getLat());
        Log.d(TAG, "sessionManager.getLng()" + sessionManager.getLng());
        if (!latitude.equalsIgnoreCase("0.0") && !longitude.equalsIgnoreCase("0.0")) {
            service.updateUserLocation(deviceId, latitude, longitude, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                }
            });
            Log.d(TAG, "onComplete: locationUpdated wtih rssi" + rssi);
        }
        //createNotification();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_ACL_CONNECTED.equals(action)) {
                rssi = intent.getIntExtra(BluetoothLeService.ACTION_ACL_CONNECTED, -1);
                Log.d(TAG, "ACTION_ACL_CONNECTED" + rssi);
                Log.d(TAG, "onReceive: in service receiver");
                //myLocation.getLocation(getApplicationContext(), locationResult);
                if (rssi < -80) {

                }
                //updateLocation();
                updateRssi();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //mGattCharacteristics.clear();
                if (mBluetoothLeService != null) {
                    //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    mBluetoothLeService.writeCharacteristic("battery");
                    mBluetoothLeService.readRssi();
                    Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                }
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
            }
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "ACTION_GATT_CONNECTED");
               /* mConnected = true;
                updateConnectionState(R.string.connected);*/
                int index = 0;
                isDisconnected = false;
                createNotification(deviceTitle + " card is Connected", "Nour");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                isDisconnected = true;
                final int[] count = {1};
                /*mConnected = false;
                updateConnectionState(R.string.disconnected);*/
                updateLocation();

                Log.d(TAG, "ACTION_GATT_DISCONNECTED");
                createNotification(deviceTitle + " card is Disconnected", "Warning");
                Handler handler = new Handler();
                int delay = 60000; //milliseconds
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.d(TAG, "isDisconnected: " + isDisconnected);
                        if (isDisconnected){
                            count[0]++;
                            if (count[0] % 6 == 0)
                                createNotification(deviceTitle + " card is Disconnected! Check Map to locate the device", "Warning");
                        }
                        handler.postDelayed(this, delay);
                    }
                }, delay);
            }
        }
    };

    public void registerReceiver() {
        Log.d(TAG, "registerReceiver called");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void unRegisterReceiver() {
        Log.d(TAG, "unRegisterReceiver called");
        unregisterReceiver(mGattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_ACL_CONNECTED);
        return intentFilter;
    }

    public MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
            Log.d(TAG, "latitude" + latitude);
            Log.d(TAG, "longitude" + longitude);
            sessionManager.saveLng(latitude);
            sessionManager.saveLng(longitude);

        }
    };

    private void updateRssi() {
        BluetoothDeviceService service = new BluetoothDeviceService();
        service.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        service.updateRssi(deviceId, String.valueOf(rssi), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            }
        });
    }

    private void createNotification() {
        TOPIC = "/topics/user_" + sessionManager.getUserID();
        Log.d(TAG, "createNotification: " + TOPIC);
        NOTIFICATION_TITLE = "Warning";
        NOTIFICATION_MESSAGE = "Connected device signals are week";
        USER_ID = sessionManager.getUserID();
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notifcationBody.put("user_id", USER_ID);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
            notification.put("click_action", ".MainActivity");
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    private void createNotification(String message, String title) {
        TOPIC = "/topics/user_" + sessionManager.getUserID();
        Log.d(TAG, "createNotification: " + TOPIC);
        NOTIFICATION_TITLE = title;
        NOTIFICATION_MESSAGE = message;
        USER_ID = sessionManager.getUserID();
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notifcationBody.put("user_id", USER_ID);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
            notification.put("click_action", ".MainActivity");
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        //Toast.makeText(GiftCopounDataActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
