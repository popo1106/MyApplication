package com.example.myapplication;

public class DataClass {

    private String userName;

    private String Description;
    private String time;
    private String imageUrl;
    private String NumClass;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClass(String userName, String description, String time, String imageUrl, String NumClass) {
        this.userName = userName;
        this.Description = description;
        this.time = time;
        this.imageUrl = imageUrl;
        this.NumClass = NumClass;
    }

    public String getUserName() {
        return userName;
    }
    public String getNumClass() {
        return NumClass;
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
