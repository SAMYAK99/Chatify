package com.projects.trending.chatify.models;

public class Users {

    String uid;
    String name ;
    String email ;
    String imageUri ;



    String status ;

    // Constructor
    public Users(String uid, String name, String email, String imageUri , String status) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageUri = imageUri;
        this.status = status  ;
    }

    // Empty Constructor which must be used with Firebase
    public Users(){

    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
