package com.example.aashimagarg.eventdistribute;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Event implements Serializable {
    private String name;
    private String host;
    private long startTime;
    private String location;
    private String description;
    private String coverImageUrl;
    private boolean hostContribution;
    private int amountNeeded;
    private String uid;

    public String getUid() {
        return uid;
    }

    public String getName() {
        return toTitleCase(name);
    }

    public String getHost() {
        return host;
    }

    public long getEpochTime() {
        return startTime;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public boolean isHostContribution() {
        return hostContribution;
    }

    public int getAmountNeeded() {
        return amountNeeded;
    }

    public Event(
                String uid,
                String name,
                String host,
                boolean hostContribution,
                long startTime,
                String location,
                String description,
                String coverImageUrl,
                int amountNeeded){
        this.uid = uid;
        this.name = name;
        this.host = host;
        this.startTime = startTime;
        this.location = location;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.hostContribution = hostContribution;
        this.amountNeeded = amountNeeded;
    }

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public String getStartDate() {
        Date date = new Date(startTime);
        DateFormat format = new SimpleDateFormat("MMMM d");
        return format.format(date);
    }

    public String getStartTime() {
        Date date = new Date(startTime);
        DateFormat format = new SimpleDateFormat("h:mm a");
        return format.format(date);
    }

    public String getFullStartTime() {
        Date date = new Date(startTime);
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy h:mm a");
        return format.format(date);
    }

    //takes in a string and capitalizes the first letter of every word:
    //eg: "beach day in san francisco" ---> "Beach Day In San Francisco"
    //for making the event titles look nice
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public Map<String, Object> toMap(){
        //Create the data we want to update
        Map<String, Object> newEvent = new HashMap<>();
        newEvent.put("uid", getUid());
        newEvent.put("name", getName());
        newEvent.put("host", getHost());
        newEvent.put("hostContribution", isHostContribution());
        newEvent.put("startTime", getEpochTime());
        newEvent.put("location", getLocation());
        newEvent.put("description", getDescription());
        newEvent.put("coverImageUrl", getCoverImageUrl());
        newEvent.put("amountNeeded", getAmountNeeded());
        return newEvent;
    }
}
