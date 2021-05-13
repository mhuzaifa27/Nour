package com.jobesk.nourv.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.jobesk.nourv.Services.BluetoothLeService;
import com.jobesk.nourv.Activities.MainActivity;
import com.jobesk.nourv.Activities.ModifyProductActivity;
import com.jobesk.nourv.FirebaseServices.BluetoothDeviceService;
import com.jobesk.nourv.FirebaseServices.ChangeEventListener;
import com.jobesk.nourv.Model.BluetoothDevice;
import com.jobesk.nourv.R;
import com.jobesk.nourv.Services.GPSTracker;
import com.jobesk.nourv.Utils.SessionManager;
import com.jobesk.nourv.Utils.ShowDialogues;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.google.android.gms.internal.zzagr.runOnUiThread;

public class ProductCardsPagerAdapter extends PagerAdapter {

    private static final String TAG = "ProductCardsPagerAdapte";
    private Activity context;
    private LayoutInflater inflater;
    private ArrayList<BluetoothDevice> deviceList;

    public ProductCardsPagerAdapter(ArrayList<BluetoothDevice> deviceList, Activity context) {
        this.context = context;
        this.deviceList = deviceList;
    }


    public int[] slideImages = {
            R.drawable.ic_keys_white,
            R.drawable.ic_wallet_white,
            R.drawable.ic_others_white,
    };

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_product_pager, container, false);
        BluetoothDevice device = deviceList.get(position);
        TextView tv_product_name = view.findViewById(R.id.tv_product_name);
        TextView tv_comment = view.findViewById(R.id.tv_comment);
        TextView tv_time = view.findViewById(R.id.tv_time);
        TextView tv_serial_no = view.findViewById(R.id.tv_serial_no);
        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_battery = view.findViewById(R.id.tv_battery);
        TextView tv_long_comment = view.findViewById(R.id.tv_long_comment);

        ImageView img_product_image = view.findViewById(R.id.img_product_image);
        if (device.getType().equalsIgnoreCase("Keys"))
            img_product_image.setImageResource(slideImages[0]);
        else if (device.getType().equalsIgnoreCase("Wallet"))
            img_product_image.setImageResource(slideImages[1]);
        else
            img_product_image.setImageResource(slideImages[2]);
        tv_product_name.setText(device.getDeviceTitle());
        tv_comment.setText(device.getComment());
        tv_time.setText(device.getTimePaired());
        tv_serial_no.setText(device.getDeviceAddress());
        tv_name.setText(device.getDeviceTitle());
        tv_long_comment.setText(device.getComment());
        //String intValue = device.getBattery().replaceAll("[^0-9]", ""); // returns 123
        tv_battery.setText(device.getBattery()+"%");

        container.addView(view);
        return view;
    }

    /*@Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }*/
    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

}
