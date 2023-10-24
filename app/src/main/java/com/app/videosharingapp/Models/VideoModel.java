package com.app.videosharingapp.Models;

import android.net.Uri;

public class VideoModel {
    Uri uri;
    int profile;
    String username;

    public VideoModel(Uri uri, int profile, String username) {
        this.uri = uri;
        this.profile = profile;
        this.username = username;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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
