package com.example.tuneinn;

public class Friends {
    private String name, email, genre, URL;

    public Friends(String name, String email, String genre, String URL) {
        this.name = name;
        this.email = email;
        this.genre = genre;
        this.URL = URL;
    }

    public Friends() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
