package com.example.iliendo.chatapp;

import android.graphics.Color;
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
    private ImageView mNoChatIcon, mSendButton;
    private EditText mMessageArea;
    private TextView mNoChatText;
    private ProgressBar mProgressbar;

//    public static String senderName;
    private static String userId;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSenderRef, mReceiverRef;
    private FirebaseRecyclerAdapter<ShowChatConversationDataItems, ChatConversationViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getDisplayName().toString().trim();

        // TODO: Add keep synced?
        // Creating many to many relation
        System.out.println(userId);
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
        mNoChatIcon = findViewById(R.id.iv_no_data);
        mSendButton = findViewById(R.id.iv_send);
        mMessageArea = findViewById(R.id.et_message);
        mNoChatText = findViewById(R.id.tv_no_data);
        mProgressbar = findViewById(R.id.pb_progressbar);

        mLinearLayoutManager = new LinearLayoutManager(Conversation.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setStackFromEnd(true);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mMessageArea.getText().toString().trim();

                if (!message.isEmpty()) {
                    // TODO: Add sound byte
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
                    mProgressbar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mNoChatIcon.setVisibility(View.GONE);
                    mNoChatText.setVisibility(View.GONE);

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
                    mProgressbar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mNoChatIcon.setVisibility(View.VISIBLE);
                    mNoChatText.setVisibility(View.VISIBLE);
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
            mMessage = (TextView) mView.findViewById(R.id.tv_message);
            mSenderName = (TextView) mView.findViewById(R.id.tv_name);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout = (LinearLayout) mView.findViewById(R.id.chat_linear_layout);
        }

        private void getSender(String title) {


            if (title.equals(userId)) {
                //Log.d("LOGGED", "getSender: ");
                params.setMargins((MainActivity.deviceWidth/ 3), 5, 10, 10);
                text_params.setMargins(15, 10, 0, 5);
                mSenderName.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_outgoing_message);
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

            if (!mSenderName.getText().equals(userId)) {
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
