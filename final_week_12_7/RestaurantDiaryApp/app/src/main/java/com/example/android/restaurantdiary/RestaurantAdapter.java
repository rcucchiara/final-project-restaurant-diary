package com.example.android.restaurantdiary;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jake on 11/2/17.
 */

public class RestaurantAdapter  extends ArrayAdapter<Restaurant> {
    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param restaurant     A List of Location objects to display in a list
     */
    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurant) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, restaurant);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.search_list_item, parent, false);
        }

        // Get the {@link location} object located at this position in the list
        Restaurant currentRestaurant = getItem(position);

        //Name
        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView restaurantView = listItemView.findViewById(R.id.search_name);
        restaurantView.setText(currentRestaurant.getName());

        //Type of Food
        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView typeOfFoodTextView = (TextView) listItemView.findViewById(R.id.search_type_of_food);
        // Get the version number from the current location object and
        // set this text on the number TextView
        typeOfFoodTextView.setText(currentRestaurant.getTypeOfFood());

        //Address
        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView addressTextView = (TextView) listItemView.findViewById(R.id.search_address);
        // Get the version number from the current location object and
        // set this text on the number TextView
        addressTextView.setText(currentRestaurant.getAddress());

        //Phone
        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView phoneTextView = (TextView) listItemView.findViewById(R.id.search_phone);
        // Get the version number from the current location object and
        // set this text on the number TextView
        phoneTextView.setText(currentRestaurant.getPhoneNumber());

        //rating
        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView ratingTextView = (TextView) listItemView.findViewById(R.id.search_rating);
        // Get the version number from the current location object and
        // set this text on the number TextView
        ratingTextView.setText(currentRestaurant.getRating());

        return listItemView;
    }

}
