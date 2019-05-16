package com.example.iliendo.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by iliendo on 5/16/19.
 */

public class SignIn extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private Button mSignOut;
    private TextView nickname;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.welcome);

        mAuth = FirebaseAuth.getInstance();
        mSignOut = findViewById(R.id.btn_signout);
        nickname =  findViewById(R.id.tf_username);

        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        // Fetch nickname of current user
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
    }

}
