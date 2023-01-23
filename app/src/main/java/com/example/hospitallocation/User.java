package com.example.hospitallocation;

public class User {

    String userName;
    Double lat;
    Double lon;
    String address;
    String date;

    public User(){}

    public User(String userName, Double lat, Double lon, String address, String date) {
        this.userName = userName;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }
}

