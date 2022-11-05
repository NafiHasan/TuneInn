package com.example.tuneinn;

import android.os.Handler;

import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.logging.LogRecord;

public class PartyInfo {
    public static ArrayList<Song> songs,hostSongs;
    public static PartyConnectUserActivity partyLan;
    public static Boolean isHost;
    public static Boolean isPlayer,isConnected;
    public static android.os.Handler handler;

    public static void init(){
        songs= new ArrayList<>();
        hostSongs= new ArrayList<>();
        partyLan= new PartyConnectUserActivity();
        isHost= false;
        isPlayer= false;
        handler= new Handler();
        isConnected=false;
    }

    public static void addSong(Song song){
        songs.add(song);
    }
}
