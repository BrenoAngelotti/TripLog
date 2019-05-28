package com.angelotti.triplog.Model;

import java.util.Date;

public class Trip {
    private String title;
    private String description;
    private Date date;
    private Type type;

    public Trip(String title, String description, Date date, Type type){
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    public Trip(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
