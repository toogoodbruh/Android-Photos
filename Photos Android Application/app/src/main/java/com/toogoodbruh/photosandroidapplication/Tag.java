package com.toogoodbruh.photosandroidapplication;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;


public class Tag implements Serializable {
    public String type;
    private String data;

    public Tag(String type, String data){
        this.type = type;
        this.data = data;
    }

    public void setData(String data){

        this.data = data;
    }

    public String getData(){

        return data;
    }
    // Method to navigate back to AlbumView

    public String toString(){

        return type+"="+data;
    }
}