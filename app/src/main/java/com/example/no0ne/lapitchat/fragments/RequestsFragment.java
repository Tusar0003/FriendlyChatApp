package com.example.no0ne.lapitchat.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.no0ne.lapitchat.R;
import com.example.no0ne.lapitchat.activity.ChatActivity;
import com.example.no0ne.lapitchat.activity.ProfileActivity;
import com.example.no0ne.lapitchat.model.Request;
import com.example.no0ne.lapitchat.model.Users;
import com.example.no0ne.lapitchat.viewHolder.FriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mRequestListRecyclerView;

    private DatabaseReference mRequestReference;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private View mMainView;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestListRecyclerView = mMainView.findViewById(R.id.recycler_view_request_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(mCurrentUserId);
        mRequestReference.keepSynced(true); // Firebase offline feature
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserReference.keepSynced(true);

        mRequestListRecyclerView.setHasFixedSize(true);
        mRequestListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Request, FriendsViewHolder> requestRecyclerViewAdapter =
                new FirebaseRecyclerAdapter<Request, FriendsViewHolder>(
                        Request.class,
                        R.layout.user_single_layout,
                        FriendsViewHolder.class,
                        mRequestReference
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Request model, int position) {
//                        if (model.getRequest_type().equalsIgnoreCase("received")) {
                            final String listUserId = getRef(position).getKey();
                            mUserReference.child(listUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String userName = dataSnapshot.child("name").getValue().toString();
                                    String userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                                    viewHolder.setName(userName);
                                    viewHolder.setImage(userThumbImage);
                                    viewHolder.setDate(dataSnapshot.child("status").getValue().toString());

                                    if (dataSnapshot.hasChild("online")) {
                                        String userOnlineStatus = dataSnapshot.child("online").getValue().toString();
                                        viewHolder.setUserOnlineStatus(userOnlineStatus);
                                    }

                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Select Options");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    switch (i) {
                                                        case 0:
                                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                            profileIntent.putExtra("user_id", listUserId);
                                                            startActivity(profileIntent);
                                                            break;
                                                        case 1:
                                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                            chatIntent.putExtra("user_id", listUserId);
                                                            chatIntent.putExtra("user_name", userName);
                                                            startActivity(chatIntent);
                                                            break;
                                                        default:
                                                            return;
                                                    }
                                                }
                                            });

                                            builder.show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
//                    }
                };

        mRequestListRecyclerView.setAdapter(requestRecyclerViewAdapter);
    }
}
