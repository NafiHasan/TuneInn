package com.example.tuneinn;

import android.media.MediaPlayer;

public class MusicPlayer
{
    static MediaPlayer instance;

    public static MediaPlayer getInstance()
    {
        if(instance == null)instance = new MediaPlayer();

        return instance;
    }
}