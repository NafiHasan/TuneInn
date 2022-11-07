package com.example.tuneinn;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecSongsViewHolder extends RecyclerView.ViewHolder {

    TextView song_name;
    ImageView DP;

    public RecSongsViewHolder(@NonNull View itemView) {
        super(itemView);

        song_name = itemView.findViewById(R.id.song_name);
        DP = itemView.findViewById(R.id.DP);
    }
}
