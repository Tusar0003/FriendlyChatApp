package com.example.no0ne.lapitchat.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.no0ne.lapitchat.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    public View mView;

    public FriendsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setName(String name) {
        TextView userNameTextView = mView.findViewById(R.id.text_view_user_name);
        userNameTextView.setText(name);
    }

    public void setDate(String date) {
        TextView userStatusTextView = mView.findViewById(R.id.text_view_user_status);
        userStatusTextView.setText(date);
    }

    public void setImage(String image) {
        CircleImageView userImageView = mView.findViewById(R.id.circle_image_view_user_list);
        Picasso.with(mView.getContext()).load(image).placeholder(R.drawable.default_image).into(userImageView);
    }

    public void setUserOnlineStatus(String onlineStatus) {
        ImageView userOnlineImageView = mView.findViewById(R.id.image_view_user_online);

        if (onlineStatus.equals("true")) {
            userOnlineImageView.setVisibility(View.VISIBLE);
        } else {
            userOnlineImageView.setVisibility(View.INVISIBLE);
        }
    }
}
