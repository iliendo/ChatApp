package com.example.iliendo.chatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by iliendo on 6/3/19.
 */

public class ChangeImage extends Fragment {

    private ImageView mImageView;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_image, container, false);

        mImageView = view.findViewById(R.id.iv_profile);
        mProgressBar = view.findViewById(R.id.pb_progressbar);

        // Tapping the image opens the gallery
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}
