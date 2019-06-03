package com.example.iliendo.chatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by iliendo on 6/3/19.
 */
// TODO: Change class name
public class ChangeEmail extends Fragment {

    private EditText mEmail;
    private EditText mPassword;
    private Button mSubmit;

    private String email;
    private String password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        mEmail = view.findViewById(R.id.et_email);
        mSubmit = view.findViewById(R.id.btn_submit);
        mPassword = view.findViewById(R.id.et_password);

        // Tapping the image opens the gallery
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = mPassword.getText().toString().trim();
                email = mEmail.getText().toString().trim();
                changeEmail(email, password);
            }
        });

        return view;
    }

    private void changeEmail(final String email, String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Now change your email address \\
                //----------------Code for Changing Email Address----------\\
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("72: email changed");
                        }
                    }
                });
            }
        });
    }
}
