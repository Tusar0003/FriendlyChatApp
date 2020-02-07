package com.example.no0ne.lapitchat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.no0ne.lapitchat.utils.GetTimeAgo;
import com.example.no0ne.lapitchat.adapter.MessageAdapter;
import com.example.no0ne.lapitchat.model.Messages;
import com.example.no0ne.lapitchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mRootReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserPushReference;

    private TextView mProfileName;
    private TextView mLastSeen;
    private CircleImageView mProfileImage;
    private EditText mChatMessage;
    private ImageButton mChatAddButton;
    private ImageButton mChatSendButton;
    private RecyclerView mMessagesList;

    private String mCurrentUserId;
    private String mChatUser;

    private final List<Messages> mList = new ArrayList<>();

    private LinearLayoutManager mLayoutManager;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true); // For custom action bar

        mRootReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(actionBarView);

        mProfileName = (TextView) findViewById(R.id.custom_bar_profile_name);
        mLastSeen = (TextView) findViewById(R.id.custom_bar_last_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_profile_image);
        mChatMessage = (EditText) findViewById(R.id.edit_text_message);
        mChatAddButton = (ImageButton) findViewById(R.id.image_button_add);
        mChatSendButton = (ImageButton) findViewById(R.id.image_button_send);
        mMessagesList = (RecyclerView) findViewById(R.id.recycler_view_messages_list);

        mAdapter = new MessageAdapter(mList);
        mLayoutManager = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLayoutManager);
        mMessagesList.setAdapter(mAdapter);

        mProfileName.setText(userName);

        mRootReference.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                if (online.equals("true")) {
                    mLastSeen.setText("Online");
                } else {
                    GetTimeAgo timeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(online);
                    String lastSeenTime = timeAgo.getTimeAgo(lastTime, getApplicationContext());

                    mLastSeen.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootReference.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chatMap = new HashMap();
                    chatMap.put("seen", false);
                    chatMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatMap);

                    mRootReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadMessages();

        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = mChatMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            mUserPushReference = mRootReference.child("Messages").child(mCurrentUserId).child(mChatUser).push();

            String currentUserRef = "Messages/" + mCurrentUserId + "/" + mChatUser;
            String chatUserRef = "Messages/" + mChatUser + "/" + mCurrentUserId;
            String pushId = mUserPushReference.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId); // The person sending the message

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
            messageUserMap.put(chatUserRef + "/" + pushId, messageMap);

            mChatMessage.setText(null);

            mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }

    private void loadMessages() {
        mRootReference.child("Messages").child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

                mList.add(messages);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
