package com.example.iliendo.chatapp;

import android.Manifest;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

public class UploadData extends AppCompatActivity {
    // Assets
    private ImageView mImageView;
    private Button mSelectImage, mUpload;
    private EditText mTitle;

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 0;
    private Firebase mRoofRef;
    private Uri mImageUri;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_data);

        Firebase.setAndroidContext(this);

        // Initialization of assets
        mImageView = findViewById(R.id.iv_profile);
        mSelectImage = findViewById(R.id.btn_select_image);
        mUpload = findViewById(R.id.btn_upload);
        mTitle = findViewById(R.id.tf_title);
        mProgressBar = findViewById(R.id.pb_upload_progress);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("G", "Upload image selected");

                // Checks for runtime permission
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    Log.d("73", "73");
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                } else {
                    Log.d("79", "else statement");
                    callGallery();
                }
                Log.d("81", "81");
            }
        });

        // Initialize database & storage
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRoofRef = new Firebase("https://chatapp-8568a.firebaseio.com/").child("User_Details").push();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chatapp-8568a.appspot.com/");

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mName = mTitle.getText().toString().trim();

                if(mName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fill in all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    Firebase childRefName = mRoofRef.child("Image_Title");
                    childRefName.setValue(mName);
                    Toast.makeText(getApplicationContext(), "Updated info", Toast.LENGTH_SHORT).show();

                }
            }
        });

        // Activates the progressbar in a new thread to prevent slowdowns
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mProgressStatus < 100){
                    mProgressStatus++;
                    android.os.SystemClock.sleep(1000);

                    // Sets the status in the ui
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(mProgressStatus);
                        }
                    });
                }
            }
        }).start();

    }

    // Check if there is permission for storage access
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    callGallery();
                } else {
                    Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);
            // User_images is the name of the folder
            StorageReference filePath = mStorageRef.child("User_Image").child(mImageUri.getLastPathSegment());

            // TODO: implement progressbar status

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    mRoofRef.child("Image_URL").setValue(downloadUri.toString());

                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            //.placeholder(R.drawable.loading)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(mImageView);
                    Toast.makeText(getApplicationContext(), "Shit is uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
