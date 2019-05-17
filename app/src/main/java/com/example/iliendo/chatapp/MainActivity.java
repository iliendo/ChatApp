package com.example.iliendo.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class  MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mNickname, mEmail, mPassword, mPasswordRepeat;
    private Button mRegister, mSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Textfields initialization
        mNickname = findViewById(R.id.et_nickname);
        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        mPasswordRepeat = findViewById(R.id.et_repeat_password);

        // Buttons initialization
        mRegister = findViewById(R.id.btn_register);
        mSignIn = findViewById(R.id.btn_signin);

        // Check if the user is already logged in, if so: redirect
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Welcome.class));
        }

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
     * @param email provided by the user
     * @param password provided by the user
     * @param passwordRepeat provided by the user to check if the password is spelled correctly
     */
    private void signUp(String email, final String password, String passwordRepeat) {
        if (matchPassword(password, passwordRepeat)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();

                                setNickname(mNickname.getText().toString().trim());
                                Toast.makeText(MainActivity.this, "The account has been registered",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                passwordLength(password);
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
                            // Error handling

                        }
                    }
                });
    }

    /**
     * Set the nickname for the account
     * @param nickname provided by the user
     */
    private void setNickname(String nickname){
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname).build();

            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.v("Name", "Problem with saving the name in the database");
                    }
                }
            });
        }
    }

    /**
     * Checks whether the password match with each other
     * @param password provided by the user
     * @param passwordRepeat provided by the user
     * @return
     */
    private boolean matchPassword(String password, String passwordRepeat){
        if(!password.equals(passwordRepeat)){
            Toast.makeText(MainActivity.this, "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void passwordLength(String password){
        if(password.length() < 6) {
            Toast.makeText(MainActivity.this, "Passwords should contain 6 characters",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
