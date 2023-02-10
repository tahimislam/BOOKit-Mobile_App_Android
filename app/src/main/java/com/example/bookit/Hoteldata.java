package com.example.bookit;

import java.io.Serializable;

public class Hoteldata implements Serializable {
    public String id, name,price,room,about,date,location,selectRoom,rating,userCount;
    public String imageview;

    public Hoteldata(){};

    public Hoteldata(String id, String name, String price, String room, String about, String date, String location, String selectRoom, String rating, String userCount, String imageview) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.room = room;
        this.about = about;
        this.date = date;
        this.location = location;
        this.selectRoom = selectRoom;
        this.rating = rating;
        this.userCount = userCount;
        this.imageview = imageview;
    }


    public float getFloatRating(){
        try {
            return Float.parseFloat(rating);
        }
        catch (Exception e){}
        return 0f;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSelectRoom() {
        return selectRoom;
    }

    public void setSelectRoom(String selectRoom) {
        this.selectRoom = selectRoom;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public String getImageview() {
        return imageview;
    }

    public void setImageview(String imageview) {
        this.imageview = imageview;
    }
}
