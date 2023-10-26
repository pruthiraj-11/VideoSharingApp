package com.app.videosharingapp.Models;

import android.net.Uri;

public class VideoModel {
    String videoURL;
    int profile;
    String username;

    public VideoModel(String videoURL, int profile, String username) {
        this.videoURL= videoURL;
        this.profile = profile;
        this.username = username;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
