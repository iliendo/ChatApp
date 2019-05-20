package com.example.iliendo.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText mTfEmail;
    private Button mBtnReturn, mBtnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        mTfEmail = findViewById(R.id.tf_email);
        mBtnReturn = findViewById(R.id.btn_return);
        mBtnReset = findViewById(R.id.btn_reset_password);

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mTfEmail.getText().toString().trim();
                resetPassword(email);
            }
        });

        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }



    private void resetPassword(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // do something when mail was sent successfully.
                            Toast.makeText(ResetPassword.this, "Check your email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            // TODO: Use try catch from firebase (look at the register page)
                            Toast.makeText(ResetPassword.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    }
