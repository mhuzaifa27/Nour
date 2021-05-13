package com.jobesk.nourv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.ShowDialogues;

public class EnterCodeActivity extends AppCompatActivity {

    private static final String TAG = "EnterCodeActivity";
    private Context context=EnterCodeActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        findViewById(R.id.tv_send_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogues.SHOW_SUCCESS_DIALOG(context,getString(R.string.code_sent_again));
            }
        });
        findViewById(R.id.btn_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,ResetPasswordActivity.class));
            }
        });
    }
}
