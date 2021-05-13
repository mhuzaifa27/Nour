package com.jobesk.nourv.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.TextView;

import com.jobesk.nourv.R;

import java.util.logging.Handler;

public class ShowDialogues {

    public static void SHOW_SUCCESS_DIALOG(Context context,String text) {

        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_success);
        TextView tv_text=alertDialog.findViewById(R.id.tv_text);
        tv_text.setText(text);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                alertDialog.dismiss();
            }
        }.start();
    }
    public static void SHOW_PAIRED_SUCCESS_DIALOG(Context context, String text, Intent intent) {

        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_success);
        TextView tv_text=alertDialog.findViewById(R.id.tv_text);
        tv_text.setText(text);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                context.startActivity(intent);
                alertDialog.dismiss();
            }
        }.start();
    }
    public static void SHOW_PAIRED_SUCCESS_DIALOG(Activity activity, String text) {
        final Dialog alertDialog = new Dialog(activity);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_success);
        TextView tv_text=alertDialog.findViewById(R.id.tv_text);
        tv_text.setText(text);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                alertDialog.dismiss();
                activity.finish();
            }
        }.start();
    }

    public static void SHOW_RINGING_DIALOG(Context context) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_ringing);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                alertDialog.dismiss();
            }
        }.start();
    }
}
