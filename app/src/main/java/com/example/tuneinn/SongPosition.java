package com.example.tuneinn;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.bumptech.glide.Glide;

public class SongPosition {
    public static int currentSongPosition = -1;
    public static String currentSongName = "No Song Playing";
    public static Song currentlyPLayingSong = null;
    public static byte[] currentArt = null;
}