package com.example.tuneinn;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class BottomPlayerFragment extends Fragment {
    public static ImageView nextButton,playPauseButton,prevButton, albumArt;
    public static TextView songFileName;
    MediaPlayer musicPlayer;
    View view;
    public BottomPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bottom_player, container, false);

        songFileName = view.findViewById(R.id.songFIleNameBottom);
        albumArt = view.findViewById(R.id.bottomFragAlbumArt);
        nextButton = view.findViewById(R.id.skipNextBottom);
        prevButton = view.findViewById(R.id.skipPrevBottom);
        playPauseButton = view.findViewById(R.id.playPauseBottom);

        songFileName.setText(SongPosition.currentSongName);

        musicPlayer = MusicPlayer.getInstance();


        playPauseButton.setImageResource(R.drawable.pause_circle);
        playPauseButton.setOnClickListener(v -> pauseOrPlayCurrentSong());



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(musicPlayer.isPlaying())playPauseButton.setImageResource(R.drawable.pause_circle);
        else playPauseButton.setImageResource(R.drawable.play_circle);

        if(SongPosition.currentArt != null)
        {
            Glide.with(this).asBitmap().load(SongPosition.currentArt).into(albumArt);
        }
        else
        {
            Glide.with(this).asBitmap().load(R.drawable.ic_baseline_music_note_24).into(albumArt);
        }
    }

    private void pauseOrPlayCurrentSong()
    {
        if(musicPlayer.isPlaying())
        {
            musicPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play_circle);
        }

        else
        {
            musicPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause_circle);
        }
    }
}