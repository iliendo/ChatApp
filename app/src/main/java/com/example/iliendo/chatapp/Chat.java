package com.example.iliendo.chatapp;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat extends AppCompatActivity {

    // TODO: make these private?
    public RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef, myRef2;
    private FirebaseRecyclerAdapter<ShowChatConversationDataItems, ChatConversationViewHolder> mFirebaseAdapter;
    public LinearLayoutManager mLinearLayoutManager;
    static String SenderName;

    private ImageView mIvAttachIcon, mIvSendIcon, mIvNoMessagesIcon;
    private EditText mMessageArea;
    private TextView mNoChat;

    // TODO: make all finals caps
    private static final int GALLERY_INTENT = 2, READ_EXTERNAL_STORAGE = 0, MULTIPLE_PERMISSIONS = 10;
    // TODO: progress dialog replacement
    private ProgressBar mProgressBar;
    private Uri mImageUri = Uri.EMPTY;

    private String pictureImagePath = "";
    final CharSequence[] options = {"Camera", "Gallery"};
    private String[] permissions = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String userId = Welcome.currentUser;
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        myRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(userId).child(getIntent().getStringExtra("nickname"));
        myRef.keepSynced(true);

        myRef2 = FirebaseDatabase.getInstance().getReference().child("Chat").child(getIntent().getStringExtra("email").replace("@", "").replace(".", "")).child(userId);
        myRef2.keepSynced(true);

        // TODO: add myref2???

        // Show the name of the person you chat with in the header
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#ffffff>" + getIntent().getStringExtra("name") + "</font>"));
        }

        // TODO: Change the names of the elements
        SenderName = getIntent().getStringExtra("name");

        mRecyclerView = findViewById(R.id.fragment_chat_recycler_view);
        mIvAttachIcon = findViewById(R.id.attachButton);
        mIvSendIcon = findViewById(R.id.sendButton);
        mIvNoMessagesIcon = findViewById(R.id.no_data_available_image);
        mMessageArea = findViewById(R.id.messageArea);
        // progressdiaglog
        mProgressBar = findViewById(R.id.progressBar3);
        mNoChat = findViewById(R.id.no_chat_text);
        mLinearLayoutManager = new LinearLayoutManager(Chat.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setStackFromEnd(true);

        mIvSendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = mMessageArea.getText().toString().trim();

                // Saves message to database
                // TODO: originally it's messageText.equals("")
                if (!messageText.isEmpty()) {
                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("message", messageText);
                    map.put("sender", Welcome.currentUser);
                    myRef.push().setValue(map);
                    myRef2.push().setValue(map);
                    mMessageArea.setText("");

                    // Recyclerview will automatically scroll to the bottom
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 500);
                }
            }
        });

        // Ask what source to use when requesting the image
        mIvAttachIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                builder.setTitle("Choose Source ");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equals("Camera")) {
                            if (checkPermissions()) {
                                callCamera();
                            }
                        }

                        if (options[i].equals("Gallery")) {
                            if (ContextCompat.checkSelfPermission(Chat.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                            } else {
                                callGallery();
                            }
                        }
                    }
                });
            }
        });
    }

    // Request all needed permissions
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    // Permission dialogs
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisssions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callGallery();
                }
                return;
            }

            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callCamera();
                }
            }
        }
    }

    private void callCamera() {
        String timeStamp = new SimpleDateFormat("yyyMMddd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;

        File file = new File(pictureImagePath);

        Uri outputFileUri = FileProvider.getUriForFile(getApplication(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", file);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        cameraIntent.putExtra(cameraIntent.EXTRA_RETURN_RESULT, true);
        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(cameraIntent, 5);
    }

    private void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Chat_Images").child(mImageUri.getLastPathSegment());

//            mProgressDialog.setMessage("Uploading...");
//            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();

                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("message", downloadUri.toString());
                    map.put("sender", Welcome.currentUser);
                    myRef.push().setValue(map);
                    myRef2.push().setValue(map);
//                    mProgressDialog.dismiss();
                }
            });
        } else if (requestCode == 5 && resultCode == RESULT_OK) {


            File imgFile = new File(pictureImagePath);
            if (imgFile.exists()) {
                Uri fileUri = Uri.fromFile(imgFile);

                StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Chat_Images").child(fileUri.getLastPathSegment());

//                mProgressDialog.setMessage("Uploading...");
//                mProgressDialog.setCancelable(false);
//                mProgressDialog.show();

                filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                        android.support.v4.util.ArrayMap<String, String> map = new android.support.v4.util.ArrayMap<>();
                        map.put("message", downloadUri.toString());
                        map.put("sender", Welcome.currentUser);
                        myRef.push().setValue(map);
                        myRef2.push().setValue(map);

//                        mProgressDialog.dismiss();
                    }
                });
            }
        } else if (requestCode == 5) {
            Toast.makeText(this, "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Handles the backbutton press
            case android.R.id.home:{
                this.onBackPressed();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ShowChatConversationDataItems, ChatConversationViewHolder>(ShowChatConversationDataItems.class, R.layout.activity_chat_bubble, ChatConversationViewHolder.class, myRef) {


            public void populateViewHolder(final ChatConversationViewHolder viewHolder, ShowChatConversationDataItems model, final int position) {

                viewHolder.getSender(model.getIncommingMessage());
                viewHolder.getMessage(model.getOutgoingMessage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        final DatabaseReference ref = mFirebaseAdapter.getRef(position);
                        ref.keepSynced(true);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String retrieve_image_url = dataSnapshot.child("message").getValue(String.class);
                                if(retrieve_image_url.startsWith("https"))
                                {
                                    //Toast.makeText(ChatConversationActivity.this, "URL : " + retrieve_image_url, Toast.LENGTH_SHORT).show();
                                    // TODO: Removed code because it provided an error, check for it later
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);

        myRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren())
                {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mIvNoMessagesIcon.setVisibility(View.GONE);
                    mNoChat.setVisibility(View.GONE);
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
                        }
                    }, 500);
                    mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v,
                                                   int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                mRecyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                                    }
                                }, 100);
                            }
                        }
                    });
                }
                else {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mIvNoMessagesIcon.setVisibility(View.VISIBLE);
                    mNoChat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static class ChatConversationViewHolder extends RecyclerView.ViewHolder {
        private final TextView message, sender;
        private final ImageView chat_image_incoming,chat_image_outgoing;
        View mView;
        final LinearLayout.LayoutParams params,text_params;
        LinearLayout layout;


        public ChatConversationViewHolder(final View itemView) {
            super(itemView);
            //Log.d("LOGGED", "ON Chat_Conversation_ViewHolder : " );
            mView = itemView;
            message = (TextView) mView.findViewById(R.id.fetch_chat_messgae);
            sender = (TextView) mView.findViewById(R.id.fetch_chat_sender);
            chat_image_incoming = (ImageView) mView.findViewById(R.id.chat_uploaded_image_incoming);
            chat_image_outgoing = (ImageView) mView.findViewById(R.id.chat_uploaded_image_outgoing);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout = (LinearLayout) mView.findViewById(R.id.chat_linear_layout);
        }

        private void getSender(String title) {


            if(title.equals(Welcome.currentUser))
            {
                //Log.d("LOGGED", "getSender: ");
                params.setMargins((MainActivity.deviceWidth/3),5,10,10);
                text_params.setMargins(15,10,0,5);
                sender.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_outcoming_message);
                sender.setText("YOU");
                chat_image_outgoing.setVisibility(View.VISIBLE);
                chat_image_incoming.setVisibility(View.GONE);

            }
            else
            {
                params.setMargins(10,0,(MainActivity.deviceWidth/3),10);
                sender.setGravity(Gravity.START);
                text_params.setMargins(60,10,0,5);
                sender.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_incoming_message);
                sender.setText(SenderName);
                chat_image_outgoing.setVisibility(View.GONE);
                chat_image_incoming.setVisibility(View.VISIBLE);
            }
        }

        private void getMessage(String title) {

            if(!title.startsWith("https"))
            {

                if(!sender.getText().equals(SenderName))
                {
                    text_params.setMargins(15,10,22,15);
                }
                else
                {
                    text_params.setMargins(65,10,22,15);
                }

                message.setLayoutParams(text_params);
                message.setText(title);
                message.setTextColor(Color.parseColor("#FFFFFF"));
                message.setVisibility(View.VISIBLE);
                chat_image_incoming.setVisibility(View.GONE);
                chat_image_outgoing.setVisibility(View.GONE);
            }
            else
            {
                if (chat_image_outgoing.getVisibility()==View.VISIBLE && chat_image_incoming.getVisibility()==View.GONE)
                {
                    chat_image_outgoing.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(itemView.getContext())
                            .load(title)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_outgoing);
                }
                else
                {
                    chat_image_incoming.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(itemView.getContext())
                            .load(title)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_incoming);
                }
            }

        }

    }

}
