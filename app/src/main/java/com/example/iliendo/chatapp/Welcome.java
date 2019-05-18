package com.example.iliendo.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Welcome extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button mSignOut, mUploadData, mViewData;
    private TextView nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        mSignOut = findViewById(R.id.btn_signout);
        nickname = findViewById(R.id.tf_username);
        mUploadData = findViewById(R.id.btn_upload_data);
        mViewData = findViewById(R.id.btn_view_data);

        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        // Fetch current user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            nickname.setText("Welcome " + user.getDisplayName());
        }

        // Button action
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UploadData.class));
            }
        });

        mViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
