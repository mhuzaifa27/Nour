package com.jobesk.nourv.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jobesk.nourv.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText et_email;
    private Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initComponents();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyField();
            }
        });
    }

    private void verifyField() {
        if(et_email.getText().toString().isEmpty()){
            et_email.setError("field required!");
        }
        else{
            changePassword();
        }
    }

    private void changePassword() {
      /*  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                    } else {
                                        Log.d(TAG, "Error password not updated")
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed")
                        }
                    }
                });*/
    }

    private void initComponents() {
        et_email=findViewById(R.id.et_email);
        btn_send=findViewById(R.id.btn_send);

    }
}
