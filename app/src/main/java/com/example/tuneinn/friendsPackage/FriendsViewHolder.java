package com.example.tuneinn.friendsPackage;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuneinn.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView userDP;
    public TextView userName;
    public TextView userGenre;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        userDP = itemView.findViewById(R.id.userDP);
        userName = itemView.findViewById(R.id.userName);
        userGenre = itemView.findViewById(R.id.userGenre);
    }
}
