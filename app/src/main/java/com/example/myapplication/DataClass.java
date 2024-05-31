package com.example.myapplication;

import java.io.Serializable;

public class DataClass implements Serializable {

    private String userName,time,Description,imageUrl,NumClass,key,Role,listObject,Urgency,descriptionPlace,whoClose,whenClose;

    private User currentUser;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClass(String userName, String description, String time, String imageUrl,String Role, String NumClass,User currentUser,String listObject, String Urgency,String descriptionPlace,String whoClose, String whenClose) {
        this.userName = userName;
        this.Description = description;
        this.time = time;
        this.imageUrl = imageUrl;
        this.NumClass = NumClass;
        this.Role = Role;
        this.currentUser = currentUser;
        this.listObject = listObject;
        this.Urgency = Urgency;
        this.descriptionPlace = descriptionPlace;
        this.whenClose = whenClose;
        this.whoClose = whoClose;
    }

    public String getWhenClose() {
        return whenClose;
    }
    public String getWhoClose() {
        return whoClose;
    }
    public String getUserName() {
        return userName;
    }
    public String getDescriptionPlace() {
        return descriptionPlace;
    }
    public String getUrgency() {
        return Urgency;
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
