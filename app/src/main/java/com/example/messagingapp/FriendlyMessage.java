package com.example.messagingapp;

public class FriendlyMessage {
    private String text;
    private String name;
    private String photoUrl;
    private String Groupname;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl , String groupname) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.Groupname = groupname;
    }

    public FriendlyMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getGroupname() {
        return Groupname;
    }

    public void setGroupname(String groupname) {
        Groupname = groupname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

