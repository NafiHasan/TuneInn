package com.example.tuneinn;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SongPosition {
    public static int currentSongPosition = -1;
    public static int selectedSongToAdd= -1;
    public static String currentSongName = "No Song Playing";
    public static Song currentlyPLayingSong = null;
    public static byte[] currentArt = null;
    public static ArrayList<Song> allSongs;
}