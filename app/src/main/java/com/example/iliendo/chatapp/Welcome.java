package com.example.iliendo.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Welcome extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button mSignOut, mUploadData, mViewData, mChat;
    private TextView nickname;

    // TODO: GETTER
    public static FirebaseDatabase mDatabase;
    static String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Creates local database when the sign in screen is skipped I guess?
        if(mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
        }

        mAuth = FirebaseAuth.getInstance();
        mSignOut = findViewById(R.id.btn_signout);
        nickname = findViewById(R.id.tf_username);
        mUploadData = findViewById(R.id.btn_upload_data);
        mViewData = findViewById(R.id.btn_view_data);
        mChat = findViewById(R.id.btn_chat);

        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        // Fetch current user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            nickname.setText("Welcome " + user.getDisplayName());
            loggedInUser = user.getEmail();
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

        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AvailableUsers.class));
            }
        });

    }

}
