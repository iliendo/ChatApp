package com.example.iliendo.chatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import static android.app.Activity.RESULT_OK;

/**
 * Created by iliendo on 6/3/19.
 */

public class ChangeImage extends Fragment {

    private ImageView mImageView;
    private ProgressBar mProgressBar;

    // Gallery constants
    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 0;

    // Firebase
    private Firebase mRoofRef;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    // User info
    private String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_image, container, false);

        mImageView = view.findViewById(R.id.iv_profile);
        mProgressBar = view.findViewById(R.id.pb_progressbar);

        // Initialize Firebase
        Firebase.setAndroidContext(getActivity());
        mAuth = FirebaseAuth.getInstance();

        name = mAuth.getCurrentUser().getDisplayName();

        mRoofRef = new Firebase("https://chatapp-8568a.firebaseio.com/").child("userDetails").child(name);
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chatapp-8568a.appspot.com/");

        // Tapping the image opens the gallery
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Checks for permission
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                } else {
                    callGallery();
                }
            }
        });

        return view;
    }

    // Check if there is permission for storage access
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callGallery();
                }
            }
        }
    }

    // If access is granted the gallery will open
    private void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    // Stores the image under user image folder inside Firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Toast.makeText(getActivity(), "Uploading...", Toast.LENGTH_SHORT).show();
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);
            // User_images is the name of the folder
            StorageReference filePath = mStorageRef.child("userImage").child(mImageUri.getLastPathSegment());

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    mRoofRef.child("imageUrl").setValue(downloadUri.toString());

                    Glide.with(getActivity())
                            .load(downloadUri)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(mImageView);
                    Toast.makeText(getActivity(), "Image has been uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress == 100.0) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
}
