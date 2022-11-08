package com.example.tuneinn.friendsPackage;

public class SongInfo {
    private String SongName;

    public SongInfo(String SongName) {
        this.SongName = SongName;
    }

    public SongInfo() {
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String SongName) {
        this.SongName = SongName;
    }
}
