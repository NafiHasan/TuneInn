package com.example.tuneinn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class PlaylistCreateActivity extends AppCompatActivity {
    EditText playlistName;
    Button createButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_playlist_name);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width* .55), ((int) (height * .25)));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        //getWindow().setAttributes();

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

                SharedPreferences sharedPreferences= getSharedPreferences("Playlists Details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(PlaylistInfo.allPlaylists);
                editor.putString("Created Playlists", json);
                editor.commit();

                //Intent intent= new Intent(PlaylistCreateActivity.this,PlaylistActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
