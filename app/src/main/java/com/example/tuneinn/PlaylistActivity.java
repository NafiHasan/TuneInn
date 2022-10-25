package com.example.tuneinn;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {
    RecyclerView playlistsRecyclerView;
    ArrayList<Playlist> playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setPlaylistsRecyclerView();
    }

    private void setPlaylistsRecyclerView() {
        playlistsRecyclerView= findViewById(R.id.playlist_recycler_view);
        playlists= PlaylistInfo.allPlaylists;

        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       // playlistsRecyclerView.setAdapter(new MusicAdapter(mySongs, getApplicationContext()));
    }
}
