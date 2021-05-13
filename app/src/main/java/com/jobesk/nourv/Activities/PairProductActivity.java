package com.jobesk.nourv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.ShowDialogues;

public class PairProductActivity extends AppCompatActivity {

    private static final String TAG = "PairProductActivity";
    private Context context=PairProductActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_product);

        findViewById(R.id.btn_pair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogues.SHOW_SUCCESS_DIALOG(context,getString(R.string.paired_successfully));
            }
        });
    }
}
