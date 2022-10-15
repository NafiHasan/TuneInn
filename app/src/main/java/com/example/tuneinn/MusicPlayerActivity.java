package com.example.tuneinn;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView musicTitle;
    TextView currentTime;
    TextView totalTime;
    SeekBar musicBar;
    ImageView pauseOrPlayButton;
    ImageView nextSongButton;
    ImageView previousSongButton;
    ImageView albumArt;
    ArrayList<Song> mySongs;
    Song currentSong;

    MediaPlayer musicPlayer = MusicPlayer.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        setID();
        setUpMusicPlayer();
        updateSeekbarAndTime();
        updateSeekbarListener();
    }

    void updateSeekbarAndTime()
    {
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicPlayer != null)
                {
                    musicBar.setProgress(musicPlayer.getCurrentPosition());
                    currentTime.setText(convert(musicPlayer.getCurrentPosition() + ""));
                }

                new Handler().postDelayed(this,100);
            }
        });

    }

    void updateSeekbarListener()
    {
        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(musicPlayer != null && fromUser) musicPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void setUpMusicPlayer()
    {
        currentSong = mySongs.get(SongPosition.currentSongPosition);

        musicTitle.setText(currentSong.getTitle());

        totalTime.setText(convert(currentSong.getDuration()));
        pauseOrPlayButton.setImageResource(R.drawable.pause_circle);
        pauseOrPlayButton.setOnClickListener(v -> pauseOrPlayCurrentSong());
        nextSongButton.setOnClickListener(v -> playNextSong());
        previousSongButton.setOnClickListener(v -> playPreviousSong());

        playMusic();
    }

    private void playMusic()
    {
        musicPlayer.reset();
        try {
            musicPlayer.setDataSource(currentSong.getData());
            musicPlayer.prepare();
            musicPlayer.start();
            musicBar.setProgress(0);
            musicBar.setMax(musicPlayer.getDuration());

        } catch (IOException e) {

        }
    }

    private void playNextSong()
    {
        if(SongPosition.currentSongPosition == mySongs.size()-1)
        {
            return;
        }

        SongPosition.currentSongPosition += 1;
        musicPlayer.reset();
        setUpMusicPlayer();
    }

    private void playPreviousSong()
    {
        if(SongPosition.currentSongPosition == 0)
        {
            return;
        }

        SongPosition.currentSongPosition -= 1;
        musicPlayer.reset();
        setUpMusicPlayer();
    }

    private void pauseOrPlayCurrentSong()
    {
        if(musicPlayer.isPlaying())
        {
            musicPlayer.pause();
            pauseOrPlayButton.setImageResource(R.drawable.play_circle);
        }

        else
        {
            musicPlayer.start();
            pauseOrPlayButton.setImageResource(R.drawable.pause_circle);
        }
    }

    public static String convert(String duration)
    {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    void setID()
    {
        musicTitle = findViewById(R.id.musicTitle);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        musicBar = findViewById(R.id.musicBar);
        pauseOrPlayButton = findViewById(R.id.pausePlay);
        nextSongButton = findViewById(R.id.nextSong);
        previousSongButton = findViewById(R.id.previousSong);
        albumArt = findViewById(R.id.albumArt);
        mySongs = (ArrayList<Song>) getIntent().getSerializableExtra("ABC");
        musicTitle.setSelected(true);
    }
}