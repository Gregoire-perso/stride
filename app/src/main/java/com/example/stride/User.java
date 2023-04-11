package com.example.stride;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.UInt;

public class User {

    protected String Uid;

    protected String birth;

    public String name;

    public String email;

    public String gender;

    public int age;

    public int racesNbr;

    public int kmNbr;

    protected List<Run> run = new ArrayList<Run>();

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

    public User(){}

    public User(String Uid, String email)
    {
        this.Uid = Uid;
        this.email = email;
        this.name = "Enter your name";
        this.birth = null;
        //this.run = new ArrayList<Time>();
        this.gender = "Prefer not to say";
        this.age = 18;
        this.racesNbr = 0;
        this.kmNbr = 0;
    }

    public void AddRun(String t)
    {
        run.add(new Run(t));
        Collections.sort(run);
    }

    public void AddRun(Run run) {
        this.run.add(run);
        Collections.sort(this.run);
    }
}



