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

/**
 * Created by iliendo on 6/3/19.
 */

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    private EditText mEmail;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mRepeatNewPassword;
    private Button mSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        mEmail = view.findViewById(R.id.et_email);
        mOldPassword = view.findViewById(R.id.et_old_password);
        mNewPassword = view.findViewById(R.id.et_new_password);
        mRepeatNewPassword = view.findViewById(R.id.et_repeat_new_password);
        mSubmit = view.findViewById(R.id.btn_submit);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: check text and submit to firebase
            }
        });
        return view;

    }
}
