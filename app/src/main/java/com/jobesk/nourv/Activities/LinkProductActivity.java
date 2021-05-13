package com.jobesk.nourv.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.jobesk.nourv.FirebaseServices.BluetoothDeviceService;
import com.jobesk.nourv.FirebaseServices.ChangeEventListener;
import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.SessionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LinkProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LinkProductActivity";
    private Context context= LinkProductActivity.this;

    private Button btn_save;
    private String bleName;
    private EditText et_name,et_serial_number,et_registeration_date,et_comment;

    private Spinner spinner_types;
    private BluetoothDeviceService bluetoothDeviceService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_product);

        bluetoothDeviceService=new BluetoothDeviceService();
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

        getIntentData();
        initComponents();

        et_serial_number.setText(bleName);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        et_registeration_date.setText(dateFormat.format(date));
        btn_save.setOnClickListener(this);
    }

    private void getIntentData() {
        bleName=getIntent().getStringExtra("deviceAddress");
    }

    private void initComponents() {
        sessionManager=new SessionManager(this);

        btn_save=findViewById(R.id.btn_save);

        et_serial_number=findViewById(R.id.et_serial_number);
        et_registeration_date=findViewById(R.id.et_registeration_date);
        et_name=findViewById(R.id.et_name);
        et_comment=findViewById(R.id.et_comment);

        spinner_types=findViewById(R.id.spinner_types);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                verifyFields();
                //startActivity(new Intent(context,PairProductActivity.class));
                break;


        }
    }

    private void verifyFields() {
        if(et_name.getText().toString().isEmpty()){
            et_name.setError("field required!");
        }
        else if(et_comment.getText().toString().isEmpty()){
            et_comment.setError("field required!");
        }
        else{
            registerDevice();
        }
    }

    private void registerDevice() {
        String type = spinner_types.getSelectedItem().toString();
        bluetoothDeviceService.registerDevice(
                sessionManager.getUserID(),
                bleName,
                et_name.getText().toString(),
                type,
                et_registeration_date.getText().toString(),
                et_comment.getText().toString(),
                et_registeration_date.getText().toString(),
                sessionManager.getLat(),
                sessionManager.getLng(),
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(context, "Device Added Successfully!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
        );
    }
}
