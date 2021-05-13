package com.jobesk.nourv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.ShowDialogues;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";
    private Context context=ResetPasswordActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SignUpActivity.class));
                //ShowDialogues.SHOW_SUCCESS_DIALOG(context,getString(R.string.password_changed_successfully));
            }
        });
    }
}
