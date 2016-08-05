package com.example.aashimagarg.eventdistribute;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String name;
    private String profileUrl;
    private String fbId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String name, String profileUrl, String fbId){
        this.uid = uid;
        this.name = name;
        this.profileUrl = profileUrl;
        this.fbId = fbId;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getFbId() {
        return fbId;
    }

    public Map<String, Object> toMap(){
        //Create the data we want to update
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("uid", getUid());
        newUser.put("name", getName());
        newUser.put("profileUrl", getProfileUrl());
        newUser.put("fbId", getFbId());
        return newUser;
    }
}
