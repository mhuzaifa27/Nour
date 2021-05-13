package com.jobesk.nourv.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jobesk.nourv.Adapters.ProductCardsPagerAdapter;
import com.jobesk.nourv.FirebaseServices.BluetoothDeviceService;
import com.jobesk.nourv.FirebaseServices.ChangeEventListener;
import com.jobesk.nourv.Model.BluetoothDevice;
import com.jobesk.nourv.R;
import com.jobesk.nourv.Services.BluetoothLeService;
import com.jobesk.nourv.Services.GPSTracker;
import com.jobesk.nourv.Services.UpdateUserLocationServiceOreo;
import com.jobesk.nourv.Utils.Constants;
import com.jobesk.nourv.Utils.MySingleton;
import com.jobesk.nourv.Utils.SessionManager;
import com.jobesk.nourv.Utils.ShowDialogues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private EditText et_name,et_phone,et_address,et_country,et_email;
    static boolean active = false;
    private static final String TAG = "hhhhhhh";
    private Context context = MainActivity.this;

    private ViewPager vp_product_pager;
    private ProductCardsPagerAdapter productCardsPagerAdapter;

    private ImageView img_add_product, img_profile_white, img_profile_grey, img_home_white, img_home_grey, img_left, img_right;

    private View header_home, header_profile;
    private RelativeLayout rl_home, rl_profile, rl_bottom_home, rl_bottom_profile, rl_no_product;

    private SessionManager sessionManager;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();

    private BluetoothDeviceService bluetoothLeService;

    private ProgressBar pb_main;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;

    public static int ITEM_SELECTED = -1;

    private GoogleApiClient googleApiClient;
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    Double latitude, longitude;

    public static boolean IS_COMING_FROM_NOTIFICATION = false;
    private TextView tv_name;

    private TextView tv_battery, last_updated, tv_long_comment;
    private Button btn_ring, btn_modify, btn_open_map,btn_change_pwd;
    private MapView map_view;
    private GoogleMap googleMap;

    /******/
    private BluetoothLeService mBluetoothLeService;
    private UpdateUserLocationServiceOreo mUpdateLocationService;
    private String mDeviceAddress;
    private int battery = 0;
    private Intent gattServiceIntent;
    private boolean isServiceConnected = false;
    private NestedScrollView device_detail;
    /******/
    private static int count = 0;
    private int rssi;

    private UpdateUserLocationServiceOreo instance;
    private Dialog alertDialog;
    private boolean isShowingDialog = false;

    //FOR NOTIFICATIONS
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAANPVIjcs:APA91bF7vG7d0Duois7UJ8I8ojoGZbGwgA01TBUT1H6CV5vDorAUpgV4Ht2xz4NBvUeeIGbR6gFHPH9CV6UfnKwCGwaoCalXsKiBISZL80TZJGMTQnX-Qpayc4ovM_E6o4FYaTy6rYYj";
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String USER_ID;
    String TOPIC = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBLEPermissions();
        setUpGClient();

        bluetoothLeService = new BluetoothDeviceService();
        bluetoothLeService.setOnChangedListener(new ChangeEventListener() {
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

        initComponents();
        //pb_main.setVisibility(View.VISIBLE);
        SHOW_CONNECTING_DIALOG(this, "Loading Data...");
        loadUserDevices();

        vp_product_pager.addOnPageChangeListener(viewPagerListener);

        img_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DeviceScanActivity.class));
                //startActivity(new Intent(context,LinkProductActivity.class));
            }
        });

        rl_bottom_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHomeView();
            }
        });
        rl_bottom_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfileView();
            }
        });
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to logout")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            FirebaseInstanceId.getInstance().deleteInstanceId();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                FirebaseAuth.getInstance().signOut();
                                sessionManager.logoutUser();
                                finish();
                                Intent mIntent = new Intent(MainActivity.this, UpdateUserLocationServiceOreo.class);
                                stopService(mIntent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyProductActivity.class);
                int index = 0;
                if (ITEM_SELECTED != -1) index = ITEM_SELECTED;
                intent.putExtra("device", deviceList.get(index));
                startActivityForResult(intent, 100);
            }
        });
        btn_open_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                if (ITEM_SELECTED != -1) index = ITEM_SELECTED;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + deviceList.get(index).getLatitude() + "," + deviceList.get(index).getLongitude() + " (" + mDeviceAddress + ")"));
                context.startActivity(intent);
            }
        });
        btn_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowingDialog) {
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();
                } else {
                    if (UpdateUserLocationServiceOreo.isRunning) {
                        UpdateUserLocationServiceOreo.instance.bleBuzzer();
                        //mUpdateLocationService.bleBuzzer();
                        /*mBluetoothLeService.writeCharacteristic("buzzer");*/
                        ShowDialogues.SHOW_RINGING_DIALOG(context);
                    }
                }
            }
        });
        btn_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void checkBLEPermissions() {
        //checkPermissions();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }


    private void loadUserDevices() {
        //pb_main.setVisibility(View.VISIBLE);
        bluetoothLeService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {
                deviceList.clear();
                if (bluetoothLeService.getCount() > 0) {
                    for (int i = 0; i < bluetoothLeService.getCount(); i++) {
                        DataSnapshot sp = bluetoothLeService.getItem(i);
                        BluetoothDevice bluetoothDevice = sp.getValue(BluetoothDevice.class);
                        if (bluetoothDevice.getUserId().equalsIgnoreCase(sessionManager.getUserID())) {
                            deviceList.add(bluetoothDevice);
                        }
                    }
                    if (deviceList.size() > 0) {
                        //pb_main.setVisibility(View.GONE);
                        HIDE_CONNECTING_DIALOG();
                        vp_product_pager.setVisibility(View.VISIBLE);
                        rl_no_product.setVisibility(View.GONE);
                        device_detail.setVisibility(View.VISIBLE);
                        productCardsPagerAdapter = new ProductCardsPagerAdapter(deviceList, MainActivity.this);

                        vp_product_pager.setAdapter(productCardsPagerAdapter);
                        if (count == 0) {
                            if (active) {
                                if (ITEM_SELECTED != -1)
                                    connectDevice(deviceList.get(ITEM_SELECTED));
                                else connectDevice(deviceList.get(0));
                            } else {
                                if (ITEM_SELECTED != -1)
                                    connectDevice(deviceList.get(ITEM_SELECTED));
                            }
                            count++;
                        } else {
                            vp_product_pager.setCurrentItem(ITEM_SELECTED);
                        }
                        Constants.MY_DEVICES = deviceList;
                    } else {
                        //pb_main.setVisibility(View.GONE);
                        HIDE_CONNECTING_DIALOG();
                        vp_product_pager.setVisibility(View.GONE);
                        rl_no_product.setVisibility(View.VISIBLE);
                        device_detail.setVisibility(View.GONE);
                    }
                } else {
                    //pb_main.setVisibility(View.GONE);
                    HIDE_CONNECTING_DIALOG();
                    vp_product_pager.setVisibility(View.GONE);
                    rl_no_product.setVisibility(View.VISIBLE);
                    device_detail.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void setProfileView() {
        //pb_main.setVisibility(View.GONE);
        HIDE_CONNECTING_DIALOG();
        header_home.setVisibility(View.GONE);
        rl_home.setVisibility(View.GONE);
        rl_bottom_home.setBackgroundColor(getResources().getColor(R.color.color_background));
        img_home_white.setVisibility(View.GONE);
        img_profile_grey.setVisibility(View.GONE);

        header_profile.setVisibility(View.VISIBLE);
        rl_profile.setVisibility(View.VISIBLE);
        rl_bottom_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        img_profile_white.setVisibility(View.VISIBLE);
        img_home_grey.setVisibility(View.VISIBLE);
    }

    private void setHomeView() {
        header_home.setVisibility(View.VISIBLE);
        rl_home.setVisibility(View.VISIBLE);
        rl_bottom_home.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        img_home_white.setVisibility(View.VISIBLE);
        img_profile_grey.setVisibility(View.VISIBLE);

        header_profile.setVisibility(View.GONE);
        rl_profile.setVisibility(View.GONE);
        rl_bottom_profile.setBackgroundColor(getResources().getColor(R.color.color_background));
        img_home_grey.setVisibility(View.GONE);
        img_profile_white.setVisibility(View.GONE);
        //loadUserDevices();
    }

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //Log.d(TAG, "onPageScrolled: "+position);

        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: " + position);
            if (ITEM_SELECTED != position) {
                ITEM_SELECTED = position;
                if (deviceList.size() > 1) {
                    if (position == 0) {
                        img_left.setVisibility(View.GONE);
                        img_right.setVisibility(View.VISIBLE);
                    } else if (ITEM_SELECTED != 0 && ITEM_SELECTED != deviceList.size() - 1) {
                        img_left.setVisibility(View.VISIBLE);
                        img_right.setVisibility(View.VISIBLE);
                    } else if(ITEM_SELECTED==1){
                        img_left.setVisibility(View.GONE);
                        img_right.setVisibility(View.GONE);
                    }
                    else {
                        img_left.setVisibility(View.VISIBLE);
                        img_right.setVisibility(View.GONE);
                    }
                }
                //vp_product_pager.setCurrentItem(position);
                connectDevice(deviceList.get(position));
                //tv_name.setText(deviceList.get(position).getDeviceTitle());
                //productCardsPagerAdapter.instantiateItem(vp_product_pager,position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };

    private void connectDevice(BluetoothDevice device) {
        mDeviceAddress = device.getDeviceAddress();
        Intent mIntent = new Intent(this, UpdateUserLocationServiceOreo.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("mDeviceAddress", mDeviceAddress);
        mBundle.putString("deviceId", device.getId());
        mBundle.putString("deviceTitle", device.getDeviceTitle());
        mIntent.putExtras(mBundle);
        //pb_main.setVisibility(View.VISIBLE);
        if (active) {
            SHOW_CONNECTING_DIALOG(this, "Connecting...");
            if (isServiceConnected) {
                //unbindService(updateLocationServiceConnector);
            }
            if (!isMyServiceRunning(UpdateUserLocationServiceOreo.class)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(mIntent);
                } else {
                    startService(mIntent);
                }
            } else {
                stopService(mIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(mIntent);
                } else {
                    startService(mIntent);
                }
            }
            //bindService(gattServiceIntent, updateLocationServiceConnector, Context.BIND_IMPORTANT);
            //bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            tv_name.setText(device.getDeviceTitle());
            tv_long_comment.setText(device.getComment());
            //last_updated.setText(getDateTime(Integer.parseInt(device.getLastUpdated())));
            last_updated.setText(device.getLastUpdated());

            if (map_view != null) {
                map_view.onCreate(null);
                map_view.onResume();
                map_view.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        try {
                            MapsInitializer.initialize(context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        googleMap = mMap;
                        // For showing a move to my location button
                        googleMap.setMyLocationEnabled(true);
                        // For dropping a marker at a point on the Map
                        LatLng sydney = new LatLng(Double.parseDouble(device.getLatitude()), Double.parseDouble(device.getLongitude()));
                        googleMap.addMarker(new MarkerOptions().position(sydney).title(device.getDeviceAddress()));
                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
            }
        } else {
            if (!isMyServiceRunning(UpdateUserLocationServiceOreo.class)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(mIntent);
                } else {
                    startService(mIntent);
                }
            } else {
                stopService(mIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(mIntent);
                } else {
                    startService(mIntent);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private String getDateTime(int timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000L);
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return date;
    }

    private void initComponents() {
        instance = new UpdateUserLocationServiceOreo();
        vp_product_pager = findViewById(R.id.vp_product_pager);

        img_add_product = findViewById(R.id.img_add_product);
        img_profile_white = findViewById(R.id.img_profile_white);
        img_profile_grey = findViewById(R.id.img_profile_grey);
        img_home_white = findViewById(R.id.img_home_white);
        img_home_grey = findViewById(R.id.img_home_grey);
        img_left = findViewById(R.id.img_left);
        img_right = findViewById(R.id.img_right);

        rl_home = findViewById(R.id.rl_home);
        rl_profile = findViewById(R.id.rl_profile);
        rl_bottom_home = findViewById(R.id.rl_bottom_home);
        rl_bottom_profile = findViewById(R.id.rl_bottom_profile);
        rl_no_product = findViewById(R.id.rl_no_product);

        header_home = findViewById(R.id.header_home);
        header_profile = findViewById(R.id.header_profile);

        sessionManager = new SessionManager(this);

        pb_main = findViewById(R.id.pb_main);

        tv_name = findViewById(R.id.tv_name);

        device_detail = findViewById(R.id.device_detail);

        //gattServiceIntent = new Intent(context, BluetoothLeService.class);
        gattServiceIntent = new Intent(context, UpdateUserLocationServiceOreo.class);

        /******/

        //tv_battery = findViewById(R.id.tv_battery);
        last_updated = findViewById(R.id.last_updated);
        tv_long_comment = findViewById(R.id.tv_long_comment);

        btn_ring = findViewById(R.id.btn_ring);
        btn_modify = findViewById(R.id.btn_modify);
        btn_open_map = findViewById(R.id.btn_open_map);
        btn_change_pwd = findViewById(R.id.btn_change_pwd);

        map_view = findViewById(R.id.map_view);

        et_name=findViewById(R.id.et_name);
        et_phone=findViewById(R.id.et_phone);
        et_address=findViewById(R.id.et_address);
        et_country=findViewById(R.id.et_country);
        et_email=findViewById(R.id.et_email);

        setProfileData();

    }

    private void setProfileData() {
        et_email.setText(sessionManager.getUserEmail());
        et_country.setText(sessionManager.getCountry());
        et_name.setText(sessionManager.getUserName());
        et_phone.setText(sessionManager.getPhoneNo());
        et_address.setText(sessionManager.getAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            registerReceiver();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unRegisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        count = 0;
        unRegisterReceiver();
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                    getMyLocation();
                } else {
                    //getMyLocation();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                String action = data.getStringExtra("action");
                if (action.equalsIgnoreCase("deleted")) {
                    SHOW_CONNECTING_DIALOG(this, "Loading Data...");
                    ITEM_SELECTED--;
                    count = 0;
                    loadUserDevices();
                }
                if (action.equalsIgnoreCase("updated")) {
                    SHOW_CONNECTING_DIALOG(this, "Loading Data...");
                    count = 0;
                    loadUserDevices();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            longitude = mylocation.getLongitude();
            sessionManager.saveLat(String.valueOf(latitude));
            sessionManager.saveLng(String.valueOf(longitude));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, MainActivity.this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback() {

                        @Override
                        public void onResult(@NonNull Result result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    //finish();
                                    break;
                            }
                        }


                    });
                }
            }
        }
    }

    /*private ServiceConnection updateLocationServiceConnector = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mUpdateLocationService = ((UpdateUserLocationServiceOreo.LocalBinder) service).getService();
            isServiceConnected = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            updateLocationServiceConnector = null;
            isServiceConnected = false;
        }

    };*/
    /***************/
    /*// Code to manage Service lifecycle.
    final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService.initialize();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                //context.finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.d(TAG, "onServiceConnected: " + mDeviceAddress);
            mBluetoothLeService.connect(mDeviceAddress);
            isServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            isServiceConnected = false;
        }

    };*/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "ACTION_GATT_CONNECTED");
               /* mConnected = true;
                updateConnectionState(R.string.connected);*/
                int index = 0;
                Log.i("ON GATT CONNECTED 2", "OK");
                /*if (ITEM_SELECTED != -1) index = ITEM_SELECTED;
                createNotification(deviceList.get(index).getDeviceTitle()+" is disconnected","Warning");*/
            } /*else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
             *//*mConnected = false;
                updateConnectionState(R.string.disconnected);*//*
                Log.d(TAG, "ACTION_GATT_DISCONNECTED");
                int index = 0;
                if (ITEM_SELECTED != -1) index = ITEM_SELECTED;
                createNotification(deviceList.get(index).getDeviceTitle()+" is disconnected","Warning");
            }*/ else if (BluetoothLeService.ACTION_ACL_CONNECTED.equals(action)) {
                rssi = intent.getIntExtra(BluetoothLeService.ACTION_ACL_CONNECTED, -1);
                Log.d(TAG, "ACTION_ACL_CONNECTED" + rssi);
                if (deviceList.size() > 0) {
                    updateRssi();
                }
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
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "ACTION_DATA_AVAILABLE");
                battery = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED: " + battery);
                updateBattery();

            }
        }
    };

    private void updateBattery() {
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
        int index = 0;
        if (ITEM_SELECTED != -1) index = ITEM_SELECTED;
        service.updateBattery(deviceList.get(index).getId(), String.valueOf(battery).trim(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
        //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
    }

    private void setProgressForTenSecond() {
       /* new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                pb_main.setVisibility(View.GONE);
            }
        };*/

        pb_main.setVisibility(View.GONE);
        HIDE_CONNECTING_DIALOG();
    }

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
        int index = 0;
        if (ITEM_SELECTED != -1) index = ITEM_SELECTED;
        service.updateRssi(deviceList.get(index).getId(), String.valueOf(rssi), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

    public void registerReceiver() {
        //bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "registerReceiver called");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //mBluetoothLeService.connect(mDeviceAddress);
           /* final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);*/
    }

    public void unRegisterReceiver() {
        Log.d(TAG, "unRegisterReceiver called");
        unregisterReceiver(mGattUpdateReceiver);
        //context.unbindService(updateLocationServiceConnector);
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void SHOW_CONNECTING_DIALOG(Context context, String text) {
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_connecting);
        alertDialog.setCancelable(true);
        TextView tv_text = alertDialog.findViewById(R.id.tv_text);
        tv_text.setText(text);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        isShowingDialog = true;
    }

    public void HIDE_CONNECTING_DIALOG() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            isShowingDialog = false;
        }
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
                        Toast.makeText(getApplicationContext(), "Request error", Toast.LENGTH_LONG).show();
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
