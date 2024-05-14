package com.example.myapplication;

public class waitingCardAp {

    private String userName;

    private String role;
    private String time;
    private String IdUser;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public waitingCardAp(String userName, String role, String time, String IdUser) {
        this.userName = userName;
        this.role = role;
        this.time = time;
        this.IdUser = IdUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIdUser() {
        return IdUser;
    }

    public void setIdUser(String idUser) {
        IdUser = idUser;
    }
}
