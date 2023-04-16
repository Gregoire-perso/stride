package com.example.stride;


import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.UInt;

public class User {

    protected String Uid;
    protected String name;
    protected String birth;
    protected String email;
    protected String gender;
    protected int age;

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

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public List<Run> getRun() {
        return run;
    }

    public int getRacesNbr() {
        int cpt = 0;
        for (Run r: run) {
            if (LocalDateTime.parse(r.getDate()).compareTo(LocalDateTime.now()) < 0) {
                cpt++;
            }
        }

        return cpt;
    }

    public long getTotalMeters() {
        long cpt = 0;
        for (Run r: run) {
            if (LocalDateTime.parse(r.getDate()).compareTo(LocalDateTime.now()) < 0) {
                cpt += r.getDistance();
            }
        }

        return cpt;
    }

    public User(){}

    public User(String Uid, String email)
    {
        this.Uid = Uid;
        this.email = email;
        this.name = "Enter your name";
        this.birth = null;
        this.gender = "Prefer not to say";
        this.age = 18;
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



