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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class  MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mNickname, mEmail, mPassword, mPasswordRepeat;
    private Button mRegister, mSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Textfields
        mNickname = findViewById(R.id.et_nickname);
        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        mPasswordRepeat = findViewById(R.id.et_repeat_password);

        // Buttons
        mRegister = findViewById(R.id.btn_register);
        mSignIn = findViewById(R.id.btn_signin);

        // Check if the user is already logged in
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), SignIn.class));
        }

        // Button interaction
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(mEmail.getText().toString().trim(), mPassword.getText().toString().trim(),
                        mPasswordRepeat.getText().toString().trim());

            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());

            }
        });

    }

    /**
     * Sign up for an account
     *
     * @param email    of user
     * @param password chosen by the user
     */
    private void signUp(String email, String password, String passwordRepeat) {
        if (matchPassword(password, passwordRepeat)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                displayName(mNickname.getText().toString().trim());
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Something went wrong",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Go to different activity when authentication is succesfull
     * @param email that the user provides
     * @param password that the user provides
     */
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent i = new Intent(MainActivity.this, SignIn.class);
                            finish();
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Display the name of the user when sign in is successful
    private void displayName(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String nickName = user.getDisplayName();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }

    // Checks whether the password match with each other
    private boolean matchPassword(String password, String passwordRepeat){
        if(!password.equals(passwordRepeat)){
            Toast.makeText(MainActivity.this, "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

}
