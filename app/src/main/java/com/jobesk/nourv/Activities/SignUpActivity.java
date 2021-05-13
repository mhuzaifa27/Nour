package com.jobesk.nourv.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.jobesk.nourv.FirebaseServices.ChangeEventListener;
import com.jobesk.nourv.FirebaseServices.UserService;
import com.jobesk.nourv.R;
import com.jobesk.nourv.Utils.SessionManager;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private Activity activity=SignUpActivity.this;

    private EditText et_name,et_email,et_phone,et_country,et_address,et_password,et_repeat_password;
    private String name,email,phone,address,country,password,confrimPassword;

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private ProgressBar pb_main;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        userService=new UserService();
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

        findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_main.setVisibility(View.VISIBLE);
                validateFields();
            }
        });
    }

    private void initComponents() {
        sessionManager = new SessionManager(this);

        pb_main=findViewById(R.id.pb_main);

        et_name=findViewById(R.id.et_name);
        et_email=findViewById(R.id.et_email);
        et_phone=findViewById(R.id.et_phone);
        et_country=findViewById(R.id.et_country);
        et_address=findViewById(R.id.et_address);
        et_password=findViewById(R.id.et_password);
        et_repeat_password=findViewById(R.id.et_repeat_password);

        mAuth = FirebaseAuth.getInstance();
    }

    private void validateFields() {
        if(et_name.getText().toString().isEmpty()){
            et_name.setError("field required!");
        }else if(et_phone.getText().toString().isEmpty()){
            et_phone.setError("field required!");
        }else if(et_address.getText().toString().isEmpty()) {
            et_address.setError("field required!");
        }else if(et_country.getText().toString().isEmpty()){
            et_country.setError("field required!");
        }else if(et_email.getText().toString().isEmpty()){
            et_email.setError("field required!");
        }else if(et_password.getText().toString().isEmpty()){
            et_password.setError("field required!");
        }else if(et_repeat_password.getText().toString().isEmpty()){
            et_repeat_password.setError("field required!");
        } else if(!et_repeat_password.getText().toString()
        .equalsIgnoreCase(et_repeat_password.getText().toString())){
            Toast.makeText(activity, "Password not match!", Toast.LENGTH_SHORT).show();
        }else{
            name=et_name.getText().toString();
            email=et_email.getText().toString();
            phone=et_phone.getText().toString();
            address=et_address.getText().toString();
            country=et_country.getText().toString();
            password=et_password.getText().toString();
            confrimPassword=et_repeat_password.getText().toString();
            registerUser();
        }
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pb_main.setVisibility(View.GONE);

                            saveUserInDatabase(mAuth.getCurrentUser().getUid());

                        } else {
                            Log.d("messagee", "onComplete: " + task.getResult());
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            pb_main.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void saveUserInDatabase(String uid) {
        userService.registerUser(email, name, uid, phone, country, address, "", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                sessionManager.saveUserID(mAuth.getCurrentUser().getUid());
                sessionManager.saveUserEmail(email);
                sessionManager.saveUserPhoneNo(phone);
                sessionManager.saveCountry(country);
                sessionManager.saveAddress(address);
                sessionManager.saveUserName(name);

                sessionManager.createLoginSession(email,mAuth.getCurrentUser().getUid());

                Toast.makeText(activity, "Login Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(activity,MainActivity.class));
                finish();
            }
        });
    }

}
