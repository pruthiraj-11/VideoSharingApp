package com.app.videosharingapp.Models;

public class UserModel {
    String profilepicURL, username, mail,password,userId,status,phonenumber;
    int followersCount,followingCount,videosCount;

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getVideosCount() {
        return videosCount;
    }

    public void setVideosCount(int videosCount) {
        this.videosCount = videosCount;
    }

    public UserModel(String profilepicURL, String username, String mail, String password, String userId, String status) {
        this.profilepicURL = profilepicURL;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.status=status;
    }
    public  UserModel(){}
    public UserModel(String username, String mail, String password) {
        this.username = username;
        this.mail = mail;
        this.password = password;
    }
    public UserModel(String phonenumber) {
        this.phonenumber=phonenumber;
    }
    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilepicURL() {
        return profilepicURL;
    }

    public void setProfilepicURL(String profilepicURL) {
        this.profilepicURL = profilepicURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
