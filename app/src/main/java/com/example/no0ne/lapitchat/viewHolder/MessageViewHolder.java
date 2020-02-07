package com.example.no0ne.lapitchat.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.no0ne.lapitchat.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by no0ne on 10/13/17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView mProfileImage;
    public TextView mMessageText;

    public MessageViewHolder(View itemView) {
        super(itemView);

        mProfileImage = itemView.findViewById(R.id.circle_image_view_profile);
        mMessageText = itemView.findViewById(R.id.text_view_message);
    }
}
