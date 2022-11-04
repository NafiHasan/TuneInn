package com.example.tuneinn;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    CircleImageView userDP;
    TextView userName, userGenre;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        userDP = itemView.findViewById(R.id.userDP);
        userName = itemView.findViewById(R.id.userName);
        userGenre = itemView.findViewById(R.id.userGenre);
    }
}
