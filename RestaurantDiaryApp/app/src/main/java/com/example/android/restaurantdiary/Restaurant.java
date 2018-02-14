package com.example.android.restaurantdiary;

/**
 * API contract for the Restaurant object.
 */

public class Restaurant {

    /** Name of restaurant */
    private String mName;

    /** Type of restaurant */
    private String mTypeOfFood;

    /** Address of restaurant */
    private String mAddress;

    /** Phone number of restaurant */
    private String mPhoneNumber;

    /** Rating of restaurant */
    private String mRating;

    public Restaurant(String name, String typeOfFood, String address, String phoneNumber, String rating){
        mName = name;
        mTypeOfFood = typeOfFood;
        mPhoneNumber = phoneNumber;
        mAddress = address;
        mRating = rating;
    }

    public String getName(){ return mName; }

    public String getTypeOfFood(){ return mTypeOfFood; }

    public String getAddress(){ return mAddress; }

    public String getPhoneNumber(){ return mPhoneNumber; }

    public String getRating(){ return mRating; }
}
