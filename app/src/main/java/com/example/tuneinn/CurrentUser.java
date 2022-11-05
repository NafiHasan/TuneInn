package com.example.tuneinn;

public class CurrentUser {
    private String name, email, url, genre;

    public CurrentUser() {
    }

    public CurrentUser(String name, String email, String genre, String url) {
        this.name = name;
        this.email = email;
        this.url = url;
        this.genre = genre;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
