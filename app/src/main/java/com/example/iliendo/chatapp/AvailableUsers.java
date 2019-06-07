package com.example.iliendo.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class AvailableUsers extends AppCompatActivity {
    private RecyclerView mRvUsers;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    public FirebaseRecyclerAdapter<ShowChatActivity, ShowChatViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_users);

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }

        // Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("userDetails");
        // Offline support of firebase database
        //mRef.keepSynced(true);

        // Initializing components
        mProgressBar = findViewById(R.id.pb_progress_bar);
        mRvUsers = findViewById(R.id.rv_users);
        mLinearLayoutManager = new LinearLayoutManager(AvailableUsers.this);

        mRvUsers.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:{
                startActivity(new Intent(getApplicationContext(), Settings.class));
                return true;
            }
            case R.id.action_logout: {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Progressbar will load while retrieving data from firebase
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ShowChatActivity, ShowChatViewHolder>(ShowChatActivity.class, R.layout.activity_single_user, ShowChatViewHolder.class, mRef) {

            public void populateViewHolder(final ShowChatViewHolder viewHolder, ShowChatActivity model, final int position) {
                // Stop progressbar when data is loaded
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);


                if (!model.getNickname().equals("Null")) {

                    // Use dataclass to get data from firebase
                    viewHolder.NickName(model.getNickname());
                    System.out.println(model.getImageUrl());
                    if (!(model.getImageUrl() == null)){
                        viewHolder.PersonImage(model.getImageUrl());
                    }

                    if (model.getEmail().equals(mAuth.getCurrentUser().getEmail())) {
                        viewHolder.LayoutHide();
                    }

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(final View v) {
                            DatabaseReference mRef = mFirebaseAdapter.getRef(position);
                            mRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String retrieveName = dataSnapshot.child("nickname").getValue(String.class);
                                    String retrieveEmail = dataSnapshot.child("email").getValue(String.class);
                                    String retrieveImage = dataSnapshot.child("imageUrl").getValue(String.class);

                                    Intent intent = new Intent(getApplicationContext(), Conversation.class);
                                    intent.putExtra("nickname", retrieveName);
                                    intent.putExtra("email", retrieveEmail);
                                    intent.putExtra("imageurl", retrieveImage);

                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

        };

        mRvUsers.setAdapter(mFirebaseAdapter);
    }

    //View Holder For Recycler View
    public static class ShowChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNickname;
        private final ImageView mIvProfile;
        private final LinearLayout mLayout;
        final LinearLayout.LayoutParams params;

        public ShowChatViewHolder(final View itemView) {
            super(itemView);
            mNickname = itemView.findViewById(R.id.tv_nickname);
            mIvProfile = itemView.findViewById(R.id.iv_profile);
            mLayout = itemView.findViewById(R.id.linear_layout);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }


        private void NickName(String title) {
            mNickname.setText(title);
        }

        private void LayoutHide() {
            params.height = 0;
            mLayout.setLayoutParams(params);

        }

        private void PersonImage(String url) {

            if (!url.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(url)
                        .thumbnail(0.5f)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mIvProfile);
            }
        }
    }
}