package com.example.tuneinn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class MusicFragment extends Fragment {
    View v;
    RecyclerView songListRecyclerView;
    ArrayList<Song> mySongs;
    Cursor cursor;
    File file;
    Song song;
    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    FrameLayout frag_bottom_player;
    Button playlists;
    TextView currentPlaylist;
    MusicAdapter musicAdapter;
    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_music, container, false);
        songListRecyclerView = v.findViewById(R.id.songs_recycler_viewf);
        playlists= v.findViewById(R.id.playlistButtonf);

        getDataFromStorage();
        addSong();
        playlists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(requireActivity().getApplicationContext(),PlaylistActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySongs = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    boolean hasAccessToStorage()
    {
        if(ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)return true;
        else return false;
    }

    void getDataFromStorage()
    {
        cursor = getContext().getContentResolver().query(uri, new String[] {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM
        }, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
    }

    void addSong()
    {
        while (cursor.moveToNext()) {
            song = new Song(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            file = new File(song.getData());

            if (file.exists()) mySongs.add(song);
        }

        SongPosition.allSongs= mySongs;
        if(PlaylistInfo.currentPlaylistPosition!= -1){
            mySongs= PlaylistInfo.allPlaylists.get(PlaylistInfo.currentPlaylistPosition).getSongs();
            currentPlaylist.setText(PlaylistInfo.allPlaylists.get(PlaylistInfo.currentPlaylistPosition).getName());
            PlaylistInfo.currentPlaylistPosition= -1;
        }
        musicAdapter = new MusicAdapter(mySongs, getContext());
        songListRecyclerView.setAdapter(musicAdapter);
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cursor.close();
        // Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
    }
}