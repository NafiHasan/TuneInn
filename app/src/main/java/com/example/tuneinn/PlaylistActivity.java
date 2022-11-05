package com.example.tuneinn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {
    RecyclerView playlistsRecyclerView;
    ArrayList<Playlist> playlists;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        setPlaylistsRecyclerView();
        initViews();
        handleEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPlaylistsRecyclerView();
    }

    private void initViews() {
        addButton= findViewById(R.id.add_new_playlist_button);
    }

    private void handleEvents() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(PlaylistActivity.this,PlaylistCreateActivity.class);
                startActivity((intent));
            }
        });
    }

    private void setPlaylistsRecyclerView() {
        playlistsRecyclerView= findViewById(R.id.playlist_recycler_view);
        //PlaylistInfo.allPlaylists= new ArrayList<>();
        playlists= PlaylistInfo.allPlaylists;

        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistsRecyclerView.setAdapter(new PlaylistAdapter(playlists, PlaylistActivity.this));
    }
}
