package com.example.tuneinn;

import java.util.ArrayList;

public class Playlist {
    String name;
    ArrayList<Song> songs;

    public Playlist(String name) {
        this.name = name;
        this.songs= new ArrayList<>();
    }

    public void addSong(Song song){
        songs.add(song);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
