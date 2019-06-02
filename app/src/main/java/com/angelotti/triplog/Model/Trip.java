package com.angelotti.triplog.Model;

import java.util.Date;

import android.arch.persistence.room.*;
import android.support.annotation.NonNull;

@Entity(tableName = "trips", foreignKeys = @ForeignKey(entity = Type.class, parentColumns = "id", childColumns  = "typeId"))
public class Trip {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private Date date;

    @Ignore
    private Type type;

    @NonNull
    @ColumnInfo(index = true)
    private int typeId;

    public Trip(String title, String description, Date date, Type type){
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.typeId = type.getId();
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
        this.typeId = type.getId();
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
