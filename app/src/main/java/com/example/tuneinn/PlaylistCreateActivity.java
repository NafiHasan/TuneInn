package com.example.tuneinn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class PlaylistCreateActivity extends AppCompatActivity {
    EditText playlistName;
    Button createButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_playlist_name);

        initViews();
        handleEvents();
    }

    private void initViews() {
        playlistName= (EditText) findViewById(R.id.playlistEditText);
        createButton= findViewById(R.id.okButtonPlaylist);
        cancelButton= findViewById(R.id.cancelButtonPlaylist);
    }

    private void handleEvents() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curName= playlistName.getText().toString().trim();
                Playlist playlist= new Playlist(curName);
                PlaylistInfo.allPlaylists.add(playlist);

                Intent intent= new Intent(PlaylistCreateActivity.this,PlaylistActivity.class);
                startActivity(intent);
            }
        });
    }

}
