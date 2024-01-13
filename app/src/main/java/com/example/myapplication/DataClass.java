package com.example.myapplication;

public class DataClass {

    private String userName;

    private String Description;
    private String time;
    private String imageUrl;
    private int NumClass;


    public DataClass(String userName, String description, String time, String imageUrl) {
        this.userName = userName;
        this.Description = description;
        this.time = time;
        this.imageUrl = imageUrl;
//        this.NumClass = NumClass;
    }

    public String getUserName() {
        return userName;
    }
//    public int getNumClass() {
//        return NumClass;
//    }



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
