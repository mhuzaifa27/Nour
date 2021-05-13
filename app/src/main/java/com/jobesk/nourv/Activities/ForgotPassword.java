package com.jobesk.nourv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jobesk.nourv.R;

public class ForgotPassword extends AppCompatActivity {

    private static final String TAG = "ForgotPassword";
    private Context context=ForgotPassword.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,EnterCodeActivity.class));
            }
        });
    }
}
