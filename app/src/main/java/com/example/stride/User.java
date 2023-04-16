package com.example.stride;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {

    protected String uid;
    protected String name;
    protected String birth;
    protected String email;
    protected String gender;
    protected int age;
    protected String profilePictureURI;

    protected List<Run> run = new ArrayList<Run>();

    //GETTERS
    public String getUid() {
        return uid;
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

    public String getProfilePictureURI() {
        return profilePictureURI;
    }

    public int computeNumberOfRace() {
        int cpt = 0;
        for (Run r: run) {
            if (LocalDateTime.parse(r.getDate()).compareTo(LocalDateTime.now()) < 0) {
                cpt++;
            }
        }

        return cpt;
    }

    public long computeRanMeters() {
        long cpt = 0;
        for (Run r: run) {
            if (LocalDateTime.parse(r.getDate()).compareTo(LocalDateTime.now()) < 0) {
                cpt += r.getDistance();
            }
        }

        return cpt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setProfilePictureURI(String profilePictureURI) {
        this.profilePictureURI = profilePictureURI;
    }

    public User(){}

    public User(String Uid, String email)
    {
        this.uid = Uid;
        this.email = email;
        this.name = "Enter your name";
        this.birth = null;
        this.gender = "Prefer not to say";
        this.age = 18;
        this.profilePictureURI = "https://firebasestorage.googleapis.com/v0/b/stride-99148.appspot.com/o/default_pp.jpg?alt=media&token=3f301eca-1a7f-41a0-9c36-c29e543fe3d7";
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



