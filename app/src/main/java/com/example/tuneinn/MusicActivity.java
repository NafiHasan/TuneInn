package com.example.tuneinn;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    RecyclerView songListRecyclerView;
    ArrayList<Song> mySongs;
    Cursor cursor;
    File file;
    Song song;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setID();

        if(hasAccessToStorage() != true)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MusicActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(MusicActivity.this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }

            else
            {
                ActivityCompat.requestPermissions(MusicActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }

            return;
        }

        getDataFromStorage();
        addSong();
    }

    void setID()
    {
        songListRecyclerView = findViewById(R.id.songs_recycler_view);
        mySongs = new ArrayList<>();
    }

    boolean hasAccessToStorage()
    {
        if(ContextCompat.checkSelfPermission(MusicActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)return true;
        else return false;
    }

    void getDataFromStorage()
    {
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
        }, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
    }

    void addSong()
    {
        while(cursor.moveToNext())
        {
            song = new Song(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            file = new File(song.getData());

            if(file.exists()) mySongs.add(song);

            songListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            songListRecyclerView.setAdapter(new MusicAdapter(mySongs, getApplicationContext()));
        }
    }
}