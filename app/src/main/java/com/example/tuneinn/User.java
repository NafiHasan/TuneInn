package com.example.tuneinn;

public class User {
    public String name, email, password, genre, URL;

    public User(){

    }
    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.genre = "Not Selected";
        this.URL = "";
    }

    public void updateUser(String name, String email, String password, String genre, String URL){
        this.name = name;
        this.email = email;
        this.password = password;
        this.genre = genre;
        this.URL = URL;
    }
}
