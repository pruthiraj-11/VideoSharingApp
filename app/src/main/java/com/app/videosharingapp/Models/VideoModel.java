package com.app.videosharingapp.Models;

import java.util.ArrayList;

public class VideoModel {
    String videoURL;
    String profileURL;
    String username;
    int likescount;
    int dislikescount;
    ArrayList<String> commentsList;

    public int getLikescount() {
        return likescount;
    }

    public void setLikescount(int likescount) {
        this.likescount = likescount;
    }

    public int getDislikescount() {
        return dislikescount;
    }

    public void setDislikescount(int dislikescount) {
        this.dislikescount = dislikescount;
    }

    public ArrayList<String> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<String> commentsList) {
        this.commentsList = commentsList;
    }

    public VideoModel(){}
    public VideoModel(String videoURL, String profileURL, String username) {
        this.videoURL= videoURL;
        this.profileURL = profileURL;
        this.username = username;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
