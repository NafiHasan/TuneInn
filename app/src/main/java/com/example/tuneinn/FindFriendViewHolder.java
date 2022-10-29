package com.example.tuneinn;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder {

    ImageView userDP;
    TextView userName, userGenre;
    Button findFriendButton;

    public FindFriendViewHolder(@NonNull View itemView) {
        super(itemView);

        userDP = itemView.findViewById(R.id.userDP);
        userName = itemView.findViewById(R.id.userName);
        userGenre = itemView.findViewById(R.id.userGenre);
    }
}
