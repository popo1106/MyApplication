package com.example.myapplication;


import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String phoneNum;
    private String idUser;
    private int level;
    private String org;


    public User(String userName, String phoneNum, String idUser, int level, String org) {
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.idUser = idUser;
        this.level = level;
        this.org = org;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

}
