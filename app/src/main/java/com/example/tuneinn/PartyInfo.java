package com.example.tuneinn;

import java.util.ArrayList;

public class PartyInfo {
    public static ArrayList<Song> songs,hostSongs;
    public static PartyConnectUserActivity partyLan;
    public static Boolean isHost;
    public static Boolean isPlayer;

    public static void init(){
        songs= new ArrayList<>();
        hostSongs= new ArrayList<>();
        partyLan= new PartyConnectUserActivity();
        isHost= false;
        isPlayer= false;
    }

    public static void addSong(Song song){
        songs.add(song);
    }
}
