package com.example.tuneinn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PartyChooseSongActivity extends AppCompatActivity {
    RecyclerView partyChooseSongRecyclerView;
    ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_choose_song);
        setPartyChooseSongRecyclerView();
        initViews();
        handleEvents();
    }


    private void initViews() {

    }

    private void handleEvents() {

    }

    private void setPartyChooseSongRecyclerView() {
        partyChooseSongRecyclerView= findViewById(R.id.chooseUserPartyRecycler);
        //PlaylistInfo.allPlaylists= new ArrayList<>();
        songs= PartyInfo.hostSongs;

        partyChooseSongRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partyChooseSongRecyclerView.setAdapter(new PartyChooseSongAdapter(songs, getApplicationContext()));
    }
}
