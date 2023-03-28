package com.example.stride;


import java.sql.Time;
import java.util.List;

public class User {

    public String Uid;

    public String name;
    public String birth;
    public String email;

    protected List<Time> run;


    public User(String Uid, String email)
    {
        this.Uid = Uid;
        this.email = email;
        this.name = "Enter your name";
        this.birth = null;
    }

    public void AddRun(Time t)
    {
        run.add(t);
    }



}



