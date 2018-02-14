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

    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurant) {
        super(context, 0, restaurant);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_has_visited, parent, false);
        }

        Restaurant currentRestaurant = getItem(position);

        TextView restaurantView = listItemView.findViewById(R.id.restaurant_name);
        restaurantView.setText(currentRestaurant.getName());

        /**
         * This changes the background color for each odd member of the list, for viewing purposes
         * */
        if(position % 2 == 1) {
            restaurantView.setBackgroundColor(Color.LTGRAY);
        }

        return listItemView;
    }

}
