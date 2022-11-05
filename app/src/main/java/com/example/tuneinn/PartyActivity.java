package com.example.tuneinn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PartyActivity extends AppCompatActivity {
    RecyclerView partyRecyclerView;
    ArrayList<Song> songs;
    Button addButton,chooseButton,hostButton;
    TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_playlist);
        setPartyRecyclerView();
        initViews();
        handleEvents();

        Handler handler= new Handler();
        handler.postDelayed(runnable,500);
    }

    public final Runnable runnable = new Runnable()
    {
        public void run()

        {
            //Toast.makeText(refresh.this,"in runnable",Toast.LENGTH_SHORT).show();
            setPartyRecyclerView();
            if(PartyInfo.isConnected){
                if(PartyInfo.isPlayer)statusText.setText("Host");
                statusText.setText("Collaborator");
            }
            else {
                statusText.setText("Not Connected");
            }
            PartyInfo.handler.postDelayed(runnable, 500);
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        setPartyRecyclerView();
    }


    private void initViews() {
        addButton= findViewById(R.id.addUserPartyButton);
        chooseButton= findViewById(R.id.chooseUserPartyButton);
        statusText= findViewById(R.id.statusTextView);
        hostButton= findViewById(R.id.partyHostButton);
    }

    private void handleEvents() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PartyInfo.isConnected){
                    Toast.makeText(PartyActivity.this, "Your Session has expired. Please re-establish WLAN and restart app", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(PartyActivity.this, PartyConnectUserActivity.class);
                    startActivity((intent));
                }
            }
        });
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(PartyActivity.this,PartyChooseSongActivity.class);
                startActivity((intent));
            }
        });
        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PartyInfo.isPlayer=true;
            }
        });
    }

    private void setPartyRecyclerView() {
        partyRecyclerView= findViewById(R.id.partyRecyclerView);
        //PlaylistInfo.allPlaylists= new ArrayList<>();
        songs= PartyInfo.songs;

        partyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partyRecyclerView.setAdapter(new PartyAdapter(songs, PartyActivity.this, PartyInfo.isPlayer));
    }
}
