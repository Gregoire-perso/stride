package com.example.stride;


public class User {

    public String Uid;

    public String name;
    public String birth;
    public String email;


    public User(String Uid, String email)
    {
        this.Uid = Uid;
        this.email = email;
        this.name = "Enter your name";
        this.birth = null;
    }


}



