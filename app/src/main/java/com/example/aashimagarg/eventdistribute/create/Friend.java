package com.example.aashimagarg.eventdistribute.create;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Friend {

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String DATA = "data";
    private static final String URL = "url";

    //Deserialize the JSON and build Friend objects
    //Friend.fromJSON("{...}") => <Friend>
    public static Friend fromJSON(JSONObject jsonObject) {
        Friend friend = new Friend();
        //Extract values form the JSON
        friend.name = jsonObject.optString(NAME);
        friend.fbId = jsonObject.optString(ID);
        JSONObject picture = jsonObject.optJSONObject(PICTURE).optJSONObject(DATA);
        //check if profile picture exists
        if (picture.length() > 0) {
            friend.profileUrl = picture.optString(URL);
        }
        //Return the friend object
        return friend;
    }

    //Friend.fromJSONArray({...}, {...}) => List<Friend>
    public static ArrayList<Friend> fromJSONArray(JSONArray array) {
        //Iterate through json array and create friends
        ArrayList<Friend> friends = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject friendJson = array.optJSONObject(i);
            Friend friend = Friend.fromJSON(friendJson);
            if (friend != null){
                friends.add(friend);
            }
        }
        return friends;
    }

    private String profileUrl;
    private String name;
    public boolean invited;
    private String fbId;

    public Friend(){
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getName() {
        return name;
    }

    public boolean isInvited() {
        return invited;
    }

    public String getFbId() {
        return fbId;
    }
}
