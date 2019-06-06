package com.example.iliendo.chatapp;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Conversation extends AppCompatActivity {

    // Attributes
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView mSendButton;
    private EditText mMessageArea;

//    public static String senderName;
    private static String userId;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSenderRef, mReceiverRef;
    private FirebaseRecyclerAdapter<ShowChatConversationDataItems, ChatConversationViewHolder> mFirebaseAdapter;

    private MediaPlayer ping;
    private MediaPlayer pong;

    private boolean isPing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Mediaplayer
        ping = MediaPlayer.create(this, R.raw.ping);
        pong = MediaPlayer.create(this, R.raw.pong);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getDisplayName().toString().trim();

        // TODO: Add keep synced?
        // Creating many to many relation
        mSenderRef = mFirebaseDatabase.getReference().child("chat").child(userId).child(getIntent().getStringExtra("nickname"));
        mReceiverRef = mFirebaseDatabase.getReference().child("chat").child(getIntent().getStringExtra("nickname")).child(userId);

        // TODO: Add code in available users to do this
        // Show the name of the person you're chatting with
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar == null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(Html.fromHtml("<font color=#fff>" + getIntent().getStringExtra("name") + "</font>"));
//        }

        // Initializing attributes
        mRecyclerView = findViewById(R.id.rv_chat);
        mSendButton = findViewById(R.id.iv_send);
        mMessageArea = findViewById(R.id.et_message);

        mLinearLayoutManager = new LinearLayoutManager(Conversation.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setStackFromEnd(true);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = mMessageArea.getText().toString().trim();

                if (!message.isEmpty()) {
                    // Play sound when message is sente
                    if (isPing == false) {
                        isPing = true;
                        ping.start();
                    } else {
                        isPing = false;
                        pong.start();
                    }

                    ArrayMap<String, String> arrayMap = new ArrayMap<>();
                    arrayMap.put("message", message);
                    arrayMap.put("sender", userId);

                    // Data will be sent to database
                    mSenderRef.push().setValue(arrayMap);
                    mReceiverRef.push().setValue(arrayMap);
                    mMessageArea.setText("");

                    // Scroll view to the most recent message
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 500);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ShowChatConversationDataItems, ChatConversationViewHolder>(ShowChatConversationDataItems.class, R.layout.activity_chat_bubble, ChatConversationViewHolder.class, mSenderRef) {

            public void populateViewHolder(final ChatConversationViewHolder viewHolder, ShowChatConversationDataItems model, final int position) {
                viewHolder.getSender(model.getSender());
                viewHolder.getMessage(model.getMessage());
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);

        mSenderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If there is a history, load the data from firebase
                if (dataSnapshot.hasChildren()) {
                    mRecyclerView.setVisibility(View.VISIBLE);

                    // Move the chat to the most recent message
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
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
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //View Holder For Recycler View
    public static class ChatConversationViewHolder extends RecyclerView.ViewHolder {
        private final TextView mMessage, mSenderName;
        private View mView;
        final LinearLayout.LayoutParams params, text_params;
        private LinearLayout layout;


        public ChatConversationViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            mMessage = mView.findViewById(R.id.tv_message);
            mSenderName = mView.findViewById(R.id.tv_name);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout = mView.findViewById(R.id.chat_linear_layout);
        }

        private void getSender(String title) {


            if (title.equals(userId)) {
                params.setMargins((MainActivity.deviceWidth/ 3), 5, 10, 10);
                text_params.setMargins(15, 10, 0, 5);
                mSenderName.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_incoming_message);
                mSenderName.setText("YOU");

            } else {
                params.setMargins(10, 0, (MainActivity.deviceWidth/ 3), 10);
                mSenderName.setGravity(Gravity.START);
                text_params.setMargins(60, 10, 0, 5);
                mSenderName.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_incoming_message);
                mSenderName.setText(userId);
            }
        }

        private void getMessage(String title) {

            if(!mSenderName.getText().equals(userId)) {
                if (!mSenderName.getText().equals(userId)) {
                    text_params.setMargins(15, 10, 22, 15);
                } else {
                    text_params.setMargins(65, 10, 22, 15);
                }

                mMessage.setLayoutParams(text_params);
                mMessage.setText(title);
                mMessage.setTextColor(Color.parseColor("#FFFFFF"));
                mMessage.setVisibility(View.VISIBLE);
            } else {
                if (mSenderName.getText().equals(userId)) {
                    text_params.setMargins(15, 10, 22, 15);
                } else {
                    text_params.setMargins(65, 10, 22, 15);
                }

                mMessage.setLayoutParams(text_params);
                mMessage.setText(title);
                mMessage.setTextColor(Color.parseColor("#FFFFFF"));
                mMessage.setVisibility(View.VISIBLE);
            }

        }

    }
}
