package com.jobesk.nourv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jobesk.nourv.R;

public class RegisterAsActivity extends AppCompatActivity {

    private static final String TAG = "RegisterAsActivity";
    private Context context=RegisterAsActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,LoginActivity.class));
                finish();
            }
        });
        findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SignUpActivity.class));
            }
        });
    }
}
