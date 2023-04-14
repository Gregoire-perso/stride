package com.example.stride;


import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class User {

    protected String Uid;

    protected String name;
    protected String birth;
    protected String email;

    protected List<Run> run = new ArrayList<Run>();

    public User(){}

    //GETTERS
    public String getUid() {
        return Uid;
    }

    public String getName() {
        return name;
    }

    public String getBirth() {
        return birth;
    }

    public String getEmail() {
        return email;
    }

    public List<Run> getRun() {
        return run;
    }

    public User(String Uid, String email)
    {
        this.Uid = Uid;
        this.email = email;
        this.name = "Enter your name";
        this.birth = null;
        //this.run = new ArrayList<Time>();
    }

    public void AddRun(String t)
    {
        run.add(new Run(t));
    }



}



