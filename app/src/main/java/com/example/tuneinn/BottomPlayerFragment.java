package com.example.tuneinn;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BottomPlayerFragment extends Fragment {
    public static ImageView nextButton,playPauseButton,prevButton, albumArt;
    public static TextView songFileName;
    MediaPlayer musicPlayer;
    byte[] arts;
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

        if(SongPosition.currentlyPLayingSong == null)songFileName.setText(SongPosition.currentSongName);
        else songFileName.setText(SongPosition.currentlyPLayingSong.getTitle());

        musicPlayer = MusicPlayer.getInstance();


        playPauseButton.setImageResource(R.drawable.pause_circle);
        playPauseButton.setOnClickListener(v -> pauseOrPlayCurrentSong());

        nextButton.setOnClickListener(v -> playNextSong());
        prevButton.setOnClickListener(v -> playPrevSong());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("A", "Bottom frag resume");

        musicPlayer = MusicPlayer.getInstance();

        songFileName = view.findViewById(R.id.songFIleNameBottom);
        albumArt = view.findViewById(R.id.bottomFragAlbumArt);
        nextButton = view.findViewById(R.id.skipNextBottom);
        prevButton = view.findViewById(R.id.skipPrevBottom);
        playPauseButton = view.findViewById(R.id.playPauseBottom);

        if(SongPosition.currentlyPLayingSong == null)songFileName.setText(SongPosition.currentSongName);
        else songFileName.setText(SongPosition.currentlyPLayingSong.getTitle());
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

            if(PartyInfo.isConnected && !PartyInfo.isPlayer){
                ExecutorService executor= Executors.newSingleThreadExecutor();
                Gson gson = new Gson();
                String json= String.valueOf(SongPosition.currentSongPosition)+"!";
                String msg= json;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(msg != null && PartyInfo.isHost && PartyInfo.partyLan.serverClass!=null){
                            PartyInfo.partyLan.serverClass.write(msg.getBytes());
                        }
                        else if(msg != null && !PartyInfo.isHost && PartyInfo.partyLan.clientClass!=null){
                            PartyInfo.partyLan.clientClass.write(msg.getBytes());
                        }
                    }
                });

            }
            else {
                musicPlayer.pause();
                playPauseButton.setImageResource(R.drawable.play_circle);
            }
        }

        else
        {
            if(PartyInfo.isConnected && !PartyInfo.isPlayer){
                ExecutorService executor= Executors.newSingleThreadExecutor();
                Gson gson = new Gson();
                String json= String.valueOf(SongPosition.currentSongPosition)+"!";
                String msg= json;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(msg != null && PartyInfo.isHost && PartyInfo.partyLan.serverClass!=null){
                            PartyInfo.partyLan.serverClass.write(msg.getBytes());
                        }
                        else if(msg != null && !PartyInfo.isHost && PartyInfo.partyLan.clientClass!=null){
                            PartyInfo.partyLan.clientClass.write(msg.getBytes());
                        }
                    }
                });
            }
            else {
                musicPlayer.start();
                playPauseButton.setImageResource(R.drawable.pause_circle);
            }
        }
    }

    private void playPrevSong()
    {
        if(PartyInfo.isConnected && !PartyInfo.isPlayer){
            ExecutorService executor= Executors.newSingleThreadExecutor();
            Gson gson = new Gson();
            String json= String.valueOf(SongPosition.currentSongPosition)+"*";
            String msg= json;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if(msg != null && PartyInfo.isHost && PartyInfo.partyLan.serverClass!=null){
                        PartyInfo.partyLan.serverClass.write(msg.getBytes());
                    }
                    else if(msg != null && !PartyInfo.isHost && PartyInfo.partyLan.clientClass!=null){
                        PartyInfo.partyLan.clientClass.write(msg.getBytes());
                    }
                }
            });
            return;
        }
        if(SongPosition.currentSongList.size() == 0)return;
        else if(SongPosition.currentSongPosition == 0)
        {
            return;
        }
        else
        {
            SongPosition.currentSongPosition -= 1;
            musicPlayer.reset();
            SongPosition.currentlyPLayingSong = SongPosition.currentSongList.get(SongPosition.currentSongPosition);
            songFileName.setText(SongPosition.currentlyPLayingSong.getTitle());
            try {
                musicPlayer.setDataSource(SongPosition.currentlyPLayingSong.getData());
                musicPlayer.prepare();
                musicPlayer.start();
                playPauseButton.setImageResource(R.drawable.pause_circle);


            } catch (IOException e) {

            }

            arts = getAlbumArt(SongPosition.currentlyPLayingSong.getData());
            SongPosition.currentArt = arts;

            if(arts != null)
            {
                Glide.with(this).asBitmap().load(arts).into(albumArt);
                //Glide.with(this).asBitmap().load(SongPosition.currentArt).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = arts;
            }

            else
            {
                Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(albumArt);
                //Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = null;
            }



        }
    }

    private void playNextSong()
    {
        if(PartyInfo.isConnected && !PartyInfo.isPlayer){
            ExecutorService executor= Executors.newSingleThreadExecutor();
            Gson gson = new Gson();
            String json= String.valueOf(SongPosition.currentSongPosition)+"^";
            String msg= json;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if(msg != null && PartyInfo.isHost && PartyInfo.partyLan.serverClass!=null){
                        PartyInfo.partyLan.serverClass.write(msg.getBytes());
                    }
                    else if(msg != null && !PartyInfo.isHost && PartyInfo.partyLan.clientClass!=null){
                        PartyInfo.partyLan.clientClass.write(msg.getBytes());
                    }
                }
            });
            return;
        }
        if(SongPosition.currentSongList.size() == 0)return;
        else if(SongPosition.currentSongPosition  == SongPosition.currentSongList.size() - 1)
        {
            return;
        }
        else
        {
            SongPosition.currentSongPosition += 1;
            musicPlayer.reset();
            SongPosition.currentlyPLayingSong = SongPosition.currentSongList.get(SongPosition.currentSongPosition);
            songFileName.setText(SongPosition.currentlyPLayingSong.getTitle());
            try {
                musicPlayer.setDataSource(SongPosition.currentlyPLayingSong.getData());
                musicPlayer.prepare();
                musicPlayer.start();
                playPauseButton.setImageResource(R.drawable.pause_circle);


            } catch (IOException e) {

            }

            arts = getAlbumArt(SongPosition.currentlyPLayingSong.getData());
            SongPosition.currentArt = arts;

            if(arts != null)
            {
                Glide.with(this).asBitmap().load(arts).into(albumArt);
                //Glide.with(this).asBitmap().load(SongPosition.currentArt).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = arts;
            }

            else
            {
                Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(albumArt);
                //Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = null;
            }

        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("B", "Bottom frag created");
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();
        return albumArt;
    }
}