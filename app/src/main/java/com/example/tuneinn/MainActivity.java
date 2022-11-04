package com.example.tuneinn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSharedPreferenceData();
        PartyInfo.init();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }, 1000);

    }

    private void loadSharedPreferenceData() {
        SharedPreferences sharedPreferences= getSharedPreferences("Playlists Details", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Created Playlists")){
            Gson gson = new Gson();
            String json = sharedPreferences.getString("Created Playlists", "");
            //PlaylistInfo.allPlaylists = gson.fromJson(json, (Type) Playlist.class);
            Type type = new TypeToken<ArrayList< Playlist >>() {}.getType();
            PlaylistInfo.allPlaylists= new Gson().fromJson(json, type);
        }
        else {
            PlaylistInfo.allPlaylists= new ArrayList<>();
        }
    }

}