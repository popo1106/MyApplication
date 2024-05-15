package com.example.myapplication;


import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String email;
    private String idUser;
    private String level;
    private String org;


    public User(String userName, String email, String idUser, String level, String org) {
        this.userName = userName;
        this.email = email;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
