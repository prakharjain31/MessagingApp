package com.example.messagingapp;

public class User {
    public String mUID;
    public String mUsername;

    public User(String mUID, String mUsername) {
        this.mUID = mUID;
        this.mUsername = mUsername;
    }

    public String getmUID() {
        return mUID;
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}
