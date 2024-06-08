package com.example.musicplayer;

public class Storage {
    private static Storage instance = null;

    private String backgroundColour;
    private int playbackSpeed;

    private Storage()
    {
        backgroundColour = "White";
        playbackSpeed = 1;
    }

    /* singleton */
    public static Storage Storage()
    {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public void setPlaybackSpeed(int playbackSpeed){
        this.playbackSpeed = playbackSpeed;
    }

    public void setBackgroundColour(String backgroundColour){
        this.backgroundColour = backgroundColour;
    }

    public int getPlaybackSpeed(){
        return playbackSpeed;
    }

    public String getBackgroundColour(){
        return backgroundColour;
    }
}
