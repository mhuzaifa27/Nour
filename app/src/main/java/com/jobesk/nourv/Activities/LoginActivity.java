package com.jobesk.nourv.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jobesk.nourv.FirebaseServices.ChangeEventListener;
import com.jobesk.nourv.FirebaseServices.UserService;
import com.jobesk.nourv.Model.User;
import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Activity activity = LoginActivity.this;

    private EditText et_email, et_password;
    private String email, password;

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private ProgressBar pb_main;
    private UserService userService;
    private Dialog alertDialog;
    private boolean isShowingDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userService = new UserService();
        userService.setOnChangedListener(new ChangeEventListener() {
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
        findViewById(R.id.tv_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ForgotPassword.class));
            }
        });
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        if (et_email.getText().toString().isEmpty()) {
            et_email.setError("field required!");
        } else if (et_password.getText().toString().isEmpty()) {
            et_password.setError("field required!");
        } else {
            email = et_email.getText().toString().trim();
            password = et_password.getText().toString();
            SHOW_CONNECTING_DIALOG(this, "Loading Data...");
            loginUser();
        }
    }

    private void initComponents() {
        sessionManager = new SessionManager(this);
        pb_main = findViewById(R.id.pb_main);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        mAuth = FirebaseAuth.getInstance();
    }

    private void loginUser() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pb_main.setVisibility(View.GONE);
                            User user = userService.getUserById(mAuth.getCurrentUser().getUid());
                            sessionManager.saveUserID(user.getUserId());
                            sessionManager.saveUserEmail(user.getEmail());
                            sessionManager.saveUserName(user.getUserName());
                            sessionManager.saveCountry(user.getPhoneNumber());
                            sessionManager.saveAddress(user.getAddress());
                            sessionManager.saveUserPhoneNo(user.getPhoneNumber());

                            sessionManager.createLoginSession(email, mAuth.getCurrentUser().getUid());
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/user_" + sessionManager.getUserID());
                            FirebaseMessaging.getInstance().subscribeToTopic("/topics/user_" + sessionManager.getUserID());
                            HIDE_CONNECTING_DIALOG();
                            Toast.makeText(activity, "Login Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(activity, MainActivity.class));
                            finish();
                        } else {
                            //Log.d("messagee", "onComplete: " + task.getResult());
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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


}
