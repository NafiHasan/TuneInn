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

public class PartyActivity extends AppCompatActivity {
    RecyclerView partyRecyclerView;
    ArrayList<Song> songs;
    Button addButton,chooseButton;
    TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_playlist);
        setPartyRecyclerView();
        initViews();
        handleEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPartyRecyclerView();
    }


    private void initViews() {
        addButton= findViewById(R.id.addUserPartyButton);
        chooseButton= findViewById(R.id.chooseUserPartyButton);
        statusText= findViewById(R.id.statusTextView);
    }

    private void handleEvents() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(PartyActivity.this,PartyConnectUserActivity.class);
                startActivity((intent));
            }
        });
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(PartyActivity.this,PartyChooseSongActivity.class);
                startActivity((intent));
            }
        });
    }

    private void setPartyRecyclerView() {
        partyRecyclerView= findViewById(R.id.partyRecyclerView);
        //PlaylistInfo.allPlaylists= new ArrayList<>();
        songs= PartyInfo.songs;

        partyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partyRecyclerView.setAdapter(new PartyAdapter(songs, getApplicationContext(), PartyInfo.isPlayer));
    }
}
