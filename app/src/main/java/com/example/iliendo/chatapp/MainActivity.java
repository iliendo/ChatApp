package com.example.iliendo.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class  MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mNickname, mEmail, mPassword, mPasswordRepeat;
    private Button mSignIn;
    private TextView mRegister, mBtnForgotPass;

    // TODO: GETTERS
    public static FirebaseDatabase mDatabase;
    public static int deviceWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        deviceWidth = metrics.widthPixels;

        // Creates local database I guess?
        if(mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
        }

        mAuth = FirebaseAuth.getInstance();

        // Textfields initialization
        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        mRegister = findViewById(R.id.btn_sign_up);
        mBtnForgotPass = findViewById(R.id.btn_forgot_password);

        // Buttons initialization
        mSignIn = findViewById(R.id.btn_signin);

        // Check if the user is already logged in, if so: redirect
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Welcome.class));
        }

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
            }
        });

        mBtnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetPassword.class));
            }
        });

    }

    /**
     * Go to different activity when authentication is successful
     * @param email that the user provides
     * @param password that the user provides
     */
    private void signIn(String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent i = new Intent(MainActivity.this, Welcome.class);
                            finish();
                            startActivity(i);
                        } else {
                            // TODO: Error handling

                        }
                    }
                });
    }
}
