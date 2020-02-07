package com.example.no0ne.lapitchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.no0ne.lapitchat.viewHolder.MessageViewHolder;
import com.example.no0ne.lapitchat.R;
import com.example.no0ne.lapitchat.model.Messages;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by no0ne on 10/13/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<Messages> mMessageList;

    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();

        String currentUserId = mAuth.getCurrentUser().getUid();

        Messages messages = mMessageList.get(position);

        String fromUser = messages.getFrom();

//        if (fromUser.equals(currentUserId)) {
//            holder.mMessageText.setBackgroundColor(Color.WHITE);
//            holder.mMessageText.setTextColor(Color.BLACK);
//        } else {
//            holder.mMessageText.setBackgroundResource(R.drawable.message_text_background);
//            holder.mMessageText.setTextColor(Color.WHITE);
//        }

        holder.mMessageText.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
