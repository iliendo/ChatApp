package com.example.iliendo.chatapp;

/**
 * Helper class to retrieve data from the database (tutorial 2)
 *
 * Created by iliendo on 5/20/19.
 */

public class DataHelper {
    private String imageUrl;

    public DataHelper(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
