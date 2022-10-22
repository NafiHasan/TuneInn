package com.example.tuneinn;

import java.io.Serializable;

public class Song implements Serializable {
    String data;
    String title;
    String artist;
    String duration;
    String album;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Song(String data, String title,String duration, String artist, String album) {
        this.data = data;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.album = album;
    }
}