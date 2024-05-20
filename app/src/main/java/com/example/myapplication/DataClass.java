package com.example.myapplication;

import java.io.Serializable;

public class DataClass implements Serializable {

    private String userName;

    private String Description;
    private String time;
    private String imageUrl;
    private String NumClass;
    private String key;
    private String Role;
    private User currentUser;
    private String listObject;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClass(String userName, String description, String time, String imageUrl,String Role, String NumClass,User currentUser,String listObject) {
        this.userName = userName;
        this.Description = description;
        this.time = time;
        this.imageUrl = imageUrl;
        this.NumClass = NumClass;
        this.Role = Role;
        this.currentUser = currentUser;
        this.listObject = listObject;
    }

    public String getUserName() {
        return userName;
    }
    public String listObject() {
        return listObject;
    }
    public String getNumClass() {
        return NumClass;
    }
    public String getRole() {
        return Role;
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public String getDescription() {
        return Description;
    }
    public String getTime() {
        return time;
    }
    public String getImageUrl() {
        return imageUrl;
    }
}
