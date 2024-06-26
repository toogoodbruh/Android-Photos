package com.toogoodbruh.photosandroidapplication;

import java.io.Serializable;
import java.util.ArrayList;


public class Album implements Serializable {
    private String albumName;
    public ArrayList<Photo> list = new ArrayList<Photo>();

    /**
     * No-args constructor for Album
     */
    public Album() {}

    /**
     * Sets Album name
     */
    public Album(String albumName){ this.albumName = albumName; }

    /**
     * Returns Album name
     */
    public String getAlbumName(){ return albumName; }

    /**
     * Sets new Album name
     */
    public void setAlbumName(String albumName){ this.albumName = albumName; }

    /**
     * Returns new Album name
     */
    public String toString(){
        return albumName;
    }
}
