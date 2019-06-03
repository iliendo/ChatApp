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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by iliendo on 6/3/19.
 */

// TODO: Rename class
public class ChangePassword extends Fragment {

    // Atributes
    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mRepeatNewPassword;
    private Button mSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // Initializing attributes
        mOldPassword = view.findViewById(R.id.et_old_password);
        mNewPassword = view.findViewById(R.id.et_new_password);
        mRepeatNewPassword = view.findViewById(R.id.et_repeat_new_password);
        mSubmit = view.findViewById(R.id.btn_submit);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: check password requirements
                // Data
                final String oldPassword = mOldPassword.getText().toString().trim();
                final String newPassword = mNewPassword.getText().toString().trim();
                final String repeatNewPassword = mRepeatNewPassword.getText().toString().trim();

                if (newPassword.equals(repeatNewPassword)) {
                    changePassword(oldPassword, newPassword);
                } else {
                    Toast.makeText(getActivity(), "New passwords don't match", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;

    }

    private void changePassword(String oldPassword, final String newPassword) {
        final FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Old password is incorrect", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Password has been changed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}

