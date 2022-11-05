package com.example.tuneinn.friendsPackage;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuneinn.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder {

    CircleImageView userDP;
    TextView userName, userGenre;


    public FindFriendViewHolder(@NonNull View itemView) {
        super(itemView);

        userDP = itemView.findViewById(R.id.userDP);
        userName = itemView.findViewById(R.id.userName);
        userGenre = itemView.findViewById(R.id.userGenre);
    }
}
