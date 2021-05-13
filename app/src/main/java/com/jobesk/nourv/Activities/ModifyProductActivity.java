package com.jobesk.nourv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.jobesk.nourv.FirebaseServices.BluetoothDeviceService;
import com.jobesk.nourv.FirebaseServices.ChangeEventListener;
import com.jobesk.nourv.Model.BluetoothDevice;
import com.jobesk.nourv.R;

public class ModifyProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ModifyProductActivity";
    private Context context = ModifyProductActivity.this;

    private ImageView img_select_keys, img_selected_keys, img_select_wallet, img_selected_wallet, img_select_others, img_selected_others, img_delete,img_back;
    private Button btn_save;
    private BluetoothDevice device;
    private TextView et_name, et_comment, et_registeration_date, et_serial_number;
    private BluetoothDeviceService bluetoothDeviceService;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);
        bluetoothDeviceService = new BluetoothDeviceService();
        bluetoothDeviceService.setOnChangedListener(new ChangeEventListener() {
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
        getIntentData();

        img_select_keys.setOnClickListener(this);
        img_selected_keys.setOnClickListener(this);
        img_select_wallet.setOnClickListener(this);
        img_selected_wallet.setOnClickListener(this);
        img_select_others.setOnClickListener(this);
        img_selected_others.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        img_back.setOnClickListener(this);

        btn_save.setOnClickListener(this);
    }

    private void getIntentData() {
        device =(BluetoothDevice) getIntent().getSerializableExtra("device");
        et_name.setText(device.getDeviceTitle());
        et_comment.setText(device.getComment());
        et_registeration_date.setText(device.getTimePaired());
        et_serial_number.setText(device.getDeviceAddress());
        type = device.getType();

        if (type.equalsIgnoreCase("Keys"))
            selectKeys();
        else if (type.equalsIgnoreCase("Wallet"))
            selectWallet();
        else
            selectOthers();
    }

    private void initComponents() {
        img_selected_keys = findViewById(R.id.img_selected_keys);
        img_select_keys = findViewById(R.id.img_select_keys);

        img_select_wallet = findViewById(R.id.img_select_wallet);
        img_selected_wallet = findViewById(R.id.img_selected_wallet);

        img_select_others = findViewById(R.id.img_select_others);
        img_selected_others = findViewById(R.id.img_selected_others);

        img_delete = findViewById(R.id.img_delete);
        img_back=findViewById(R.id.img_back);

        et_name = findViewById(R.id.et_name);
        et_comment = findViewById(R.id.et_comment);
        et_registeration_date = findViewById(R.id.et_registeration_date);
        et_serial_number = findViewById(R.id.et_serial_number);

        btn_save = findViewById(R.id.btn_save);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_select_keys:
                selectKeys();
                break;
            case R.id.img_select_wallet:
                selectWallet();
                break;
            case R.id.img_select_others:
                selectOthers();
                break;
            case R.id.btn_save:
                bluetoothDeviceService.modifyDevice(device.getId(), et_name.getText().toString(), type, et_comment.getText().toString());
                Toast.makeText(context, "Device updated successfully!", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("action","updated");
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.img_delete:
                new AlertDialog.Builder(context)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to remove this device?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                bluetoothDeviceService.deleteDevice(device.getId());
                                Toast.makeText(context, "Device removed!", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent();
                                intent.putExtra("action","deleted");
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.img_back:
                onBackPressed();
                break;


        }
    }

    private void selectKeys() {
        img_select_keys.setVisibility(View.GONE);
        img_selected_keys.setVisibility(View.VISIBLE);
        img_select_wallet.setVisibility(View.VISIBLE);
        img_selected_wallet.setVisibility(View.GONE);
        img_select_others.setVisibility(View.VISIBLE);
        img_selected_others.setVisibility(View.GONE);
        type = "Keys";
    }

    private void selectWallet() {
        img_select_keys.setVisibility(View.VISIBLE);
        img_selected_keys.setVisibility(View.GONE);
        img_select_wallet.setVisibility(View.GONE);
        img_selected_wallet.setVisibility(View.VISIBLE);
        img_select_others.setVisibility(View.VISIBLE);
        img_selected_others.setVisibility(View.GONE);
        type = "Wallet";
    }

    private void selectOthers() {
        img_select_keys.setVisibility(View.VISIBLE);
        img_selected_keys.setVisibility(View.GONE);
        img_select_wallet.setVisibility(View.VISIBLE);
        img_selected_wallet.setVisibility(View.GONE);
        img_select_others.setVisibility(View.GONE);
        img_selected_others.setVisibility(View.VISIBLE);
        type = "Others";
    }
}
