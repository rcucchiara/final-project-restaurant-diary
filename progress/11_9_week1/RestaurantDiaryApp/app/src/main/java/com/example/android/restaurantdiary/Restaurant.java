package com.example.android.restaurantdiary;

/**
 * API contract.
 */

public class Restaurant {
    private String mName;
    // rest of the private stuff

    public Restaurant(String name){
        mName = name;
    }

    public String getName(){ return mName; }
}
