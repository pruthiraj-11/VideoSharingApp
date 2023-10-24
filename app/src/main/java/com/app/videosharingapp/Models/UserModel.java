package com.app.videosharingapp.Models;

public class UserModel {
    String profilepic, username, mail,password,userId,status,phonenumber;

    public UserModel(String profilepic, String username, String mail, String password, String userId, String lastmessage,String status) {
        this.profilepic = profilepic;
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

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
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
